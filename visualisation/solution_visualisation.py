import sys
import matplotlib.pyplot as plt

import pandas as pd

if __name__ == '__main__':
    colNames = ['id', 'x', 'y', 'demand', 'ready_time', 'due_date', 'service_time']
    inputFile = sys.argv[1]
    df = pd.read_table(inputFile, sep="\s+", names=colNames, index_col=0)

    # now read routes
    lines = []
    routesFile = sys.argv[2]
    with open(routesFile) as f:
        lines = f.readlines()

        routes = [line.strip().split(",") for line in lines]

        for route in routes:
            customers = df.loc[route]
            xCoords = customers["x"].astype('int64')
            yCoords = customers["y"].astype('int64')
            plt.plot(xCoords, yCoords, '-o')
        plt.show()
