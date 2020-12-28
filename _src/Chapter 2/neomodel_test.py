#Sample code to demonstrate Neo4j Object Graph Mapper
from neomodel import (StructuredNode, StringProperty, IntegerProperty,
    RelationshipTo, RelationshipFrom)

class Movie(StructuredNode):
    name = StringProperty(unique_index=True, required=True)
    actors = RelationshipFrom('Actor', 'ACTED_IN')

class Actor(StructuredNode):
    name = StringProperty(unique_index=True, required=True)
    acted = RelationshipTo('Movie', 'ACTED_IN')

titanic = Movie(name="Titanic").save()