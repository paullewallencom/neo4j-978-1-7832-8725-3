require 'rubygems'
require 'neography'
@graph = Neography::Rest.new({ :protocol       => 'http://',
                               :server         => "localhost",
                               :port           => 7474,
})		
node1 = @graph.create_node("name" => "A")
