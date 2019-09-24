# RunRoute
A web service for generating runnning routes based on a set of preferences.

OpenStreetMap data is used to build the underlying graph, which is then searched for features that align with the
user preferences. This is achieved by assigning a score to each explored graph edge.

A greedy search process and the ILS metaheuristic is then used to discover and return suitable routes. 

The repo for the corresponding client component is located here: https://github.com/everett-lee/RunRouteClient/

## A generated route 
![A generated route](/route.jpg)

