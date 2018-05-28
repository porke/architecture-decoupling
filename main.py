import sys

def main(args):
    argument_pairs = filter(lambda str: not str, " ".join(args).split("--"))

    # Identify the --task 
    # Parse the remaining args
    # Relay to the appropriate task

    print("Hello World: " + str(len(argument_pairs)))

def process_argument(arg):
    return

if __name__ == "__main__":
    main(sys.argv[1:])

