import sys
import glob
import subprocess
import os
import json
import itertools
from collections import Counter
import csv

def export_hotspot(hotspot_path):    
    my_env = os.environ.copy()    
    filename = os.path.basename(os.path.normpath(hotspot_path))
    hotspot_name = os.path.splitext(filename)[0]
    subprocess.run(["dv8-console", "export-matrix", hotspot_name + "-sdsm", "-project", hotspot_path, "-out", "hotspot-" + hotspot_name + ".json"], env=my_env, shell=True)    

def find_hotspots(project_path):    
    return glob.glob(project_path + '\\arch-issues/**/*.dv8-proj', recursive=True)

def list_exported_hotspots():
    return glob.glob('hotspot-*.json', recursive=True)

def load_files_from_hotspot(hotspot_file):
    with open(hotspot_file) as file:
        hotspot = json.load(file)
        return hotspot["variables"]    

def count_hotspots_per_file(list_of_files_per_hotspot):
    complete_list = list(itertools.chain(*list_of_files_per_hotspot))
    return Counter(complete_list)

def dump_to_csv(hotspots_per_file):
    with open('hotspot-stats.csv', 'w') as out_file:
        hotspot_writer = csv.writer(out_file, lineterminator='\n')
        for hotspot in hotspots_per_file: 
            hotspot_writer.writerow([hotspot, hotspots_per_file[hotspot]])

def export_hotspots(project_path):
    hotspot_files = find_hotspots(project_path)
    print("Hotspot count: " + str(len(hotspot_files)))
    for file in hotspot_files:
        export_hotspot(file)
    exported_hotspots = list_exported_hotspots()
    hotspots_per_file = count_hotspots_per_file([load_files_from_hotspot(hotspot) for hotspot in exported_hotspots])
    dump_to_csv(hotspots_per_file)    

# This tool takes the hotspot patterns obtained from dv8-console, exports them to json and aggregates them
# so that the end output is a list of files and the number of hotspots the file is a part of
def main(args):
    export_hotspots(args[0])

if __name__ == "__main__":
    main(sys.argv[1:])
