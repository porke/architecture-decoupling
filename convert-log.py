import sys
import re
import json
import operator
from collections import Counter
from functools import reduce

commit_index = 0
total_commits = 0

def extract_file_contents(path):
    with open(path, 'r') as log_file:
        file_contents = log_file.read()
    return file_contents

def extract_files(commit):
    matches = re.split("[0-9]+\t[0-9]+\t(.*)\n", commit)
    matches = filter(None, matches[1:])
    matches = filter(lambda line: line != "\n", matches)
    matches = filter(lambda line: "{" not in line, matches)
    matches = filter(lambda line: line.endswith("java"), matches)
    
    return list(matches)

def extract_connections(all_files, commit):            
    commit_files = extract_files(commit)
    connections = [{"src" : all_files[src], "dest" : all_files[dest]} for src in commit_files for dest in commit_files if src != dest]

    global commit_index
    commit_index = commit_index + 1
    print("\r" + str(commit_index) + "/" + str(total_commits) + " commits processed with " + str(len(commit_files)) + " files")

    return connections

def dump_to_json(output_path, files, connections):    
    with open(output_path, 'w') as output_file:
        dumpster = {
            "@schema-version" : 1.0,
            "name:" : output_path.replace(".json", ""),
            "variables" : [f.replace(".", "_").replace("/", ".") for f in files],
            "cells" : connections
            }
        json.dump(dumpster, output_file, indent=4)        

def cumulate_connections(connections):
    counted_connections = Counter([(connection["src"], connection["dest"]) for connection in connections])
    return [{"src" : k[0], "dest" : k[1], "values" : {"Cochange" : float(v)}} for (k,v) in counted_connections.items()]

def convert_log(path):
    file_contents = extract_file_contents(path)
    commits = list(filter(None, file_contents.split("commit")))
    global total_commits 
    total_commits = len(commits)
    print(str(total_commits) + " commits to process...")

    file_set = set(reduce(operator.add, [extract_files(commit) for commit in commits]))
    files = dict(zip(file_set, range(0, len(file_set))))    
    print(str(len(files)) + " files extracted")

    connections = list(reduce(operator.add, filter(lambda xs: xs != [], [extract_connections(files, commit) for commit in commits])))
    cumulated_connections = cumulate_connections(connections)
    print(str(len(connections)) + " cumulative connections extracted")

    dump_to_json(path.replace("txt", "json"), files, cumulated_connections)

def main(args):
    convert_log(args[0])

if __name__ == "__main__":
    main(sys.argv[1:])

