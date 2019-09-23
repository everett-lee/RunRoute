# RunRoute

A routing web service for runners. This aims to generate routes based on a set of preferences.

OpenStreetMap data is used to build the underlying graph, which is then searched for features that align with the
user preferences.

A greedy search process and the ILS metaheuristic is used to achieve this.

The repo for the corresponding client component is located here: https://github.com/everett-lee/RunRouteClient/
