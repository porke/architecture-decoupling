import sys
import re
import json
import operator
import xml.etree.ElementTree as ET
from collections import Counter
from functools import reduce

def extract_file_contents(path):
    with open(path, 'r') as log_file:
        file_contents = log_file.read()
    return file_contents

def extract_files(commit):
    matches = re.split("[0-9]+\t[0-9]+\t(.*)\n", commit)
    # remove commit headers, empty lines move operations
    matches = filter(None, matches[1:])
    matches = filter(lambda line: line != "\n", matches)
    matches = filter(lambda line: "{" not in line, matches)
    
    return list(matches)

def extract_connections(all_files, commit):            
    commit_files = {f.replace(".", "_").replace("/", ".") for f in extract_files(commit)}
    connections = [{"src" : all_files[src], "dest" : all_files[dest]} 
                        for src in commit_files 
                        for dest in commit_files 
                        if src != dest and src in all_files and dest in all_files]
    return connections

def dump_to_json(output_path, files, connections):        
    with open(output_path, 'w') as output_file:
        payload = {
            "@schema-version" : 1.0,
            "name:" : output_path.replace(".json", ""),
            "variables" : [f.replace(".", "/").replace("_", ".") for f in files],
            "cells" : connections
            }
        json.dump(payload, output_file)
        # pretty print for debugging:
        #json.dump(payload, output_file, indent=4)

def cumulate_connections(connections):
    counted_connections = Counter([(connection["src"], connection["dest"]) for connection in connections])
    return [{"src" : k[0], "dest" : k[1], "values" : {"Cochange" : float(v)}} for (k,v) in counted_connections.items()]

def load_file_from_cytoscape_xml(path):
    tree = ET.parse(path)
    root = tree.getroot()
    
    files = set({})    
    for node in root.findall(".//*[@name='longName']"):
        file = node.get("value")        
        files.add(file)
    return set({f.replace(".", "_").replace("/", ".") for f in files})

# The exported json files has the same format with the root being the root of the repository folder
# Cytoscape has to have files in the following format: "tools/src/main/java/org/apache/pdfbox/tools/TextToPDF.java"
# where "." is the repository root folder
def convert_log(log_path, structure_path):
    file_contents = extract_file_contents(log_path)
    commits = list(filter(None, file_contents.split("commit")))    
    print(str(len(commits)) + " commits to process...")

    structure_files = load_file_from_cytoscape_xml(structure_path)    
    print(str(len(structure_files)) + " structure files extracted")

    files = dict(zip(structure_files, range(0, len(structure_files))))
    connections = list(reduce(operator.add, filter(lambda xs: xs != [], [extract_connections(files, commit) for commit in commits]), []))
    cumulated_connections = cumulate_connections(connections)
    print(str(len(connections)) + " cumulative connections extracted")

    dump_to_json(log_path.replace("txt", "json"), files, cumulated_connections)

def main(args):
    convert_log(args[0], args[1])

if __name__ == "__main__":
    main(sys.argv[1:])

