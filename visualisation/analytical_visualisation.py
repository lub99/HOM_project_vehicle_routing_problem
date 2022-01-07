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

        allCustomersXStr = df["x"].tolist()[1:]
        allCustomersYStr = df["y"].tolist()[1:]
        allCustomersX = [int(x) for x in allCustomersXStr]
        allCustomersY = [int(y) for y in allCustomersYStr]

        fig = plt.figure()
        axes = fig.add_subplot(111)

        axes.scatter(allCustomersX, allCustomersY)
        # plt.show()

        allPreviousGraphs = []
        for route in routes:
            customersOnRoute = df.loc[route]
            xCoords = customersOnRoute["x"].astype('int64')
            yCoords = customersOnRoute["y"].astype('int64')
            axes.plot(xCoords, yCoords, '-o')
            ax = plt.gca()
            ax.set_xlim([0, 80])
            ax.set_ylim([0, 80])
            plt.show()

            fig = plt.figure()
            axes = fig.add_subplot(111)
            allPreviousGraphs.append((xCoords, yCoords))
            # draw all previous rotes on same graph
            axes.scatter(allCustomersX, allCustomersY)
            for graph in allPreviousGraphs:
                axes.plot(graph[0], graph[1], '-o')
                ax = plt.gca()
                ax.set_xlim([0, 80])
                ax.set_ylim([0, 80])
