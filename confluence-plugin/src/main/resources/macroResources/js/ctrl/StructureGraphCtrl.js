angular.module("DoC").controller("StructureGraphCtrl", function($scope, $http, $rootScope, $state,
                                                                $filter, $timeout, $location, javadocEntities) {
    $scope.loading = true;
    var root;
    var margin = {top: -5, right: -5, bottom: -5, left: -5};
    var width = $(window).width() - margin.left - margin.right,
        height = $(window).height() - 200 - margin.top - margin.bottom;

    var force = d3.layout.force()
        .linkDistance(50)
        .charge(-1200)
        .gravity(0.5)
        .alpha(100)
        .size([width + margin.left + margin.right, height + margin.top + margin.bottom])
        .on("tick", tick);

    var svg = d3.select("#doc_structureGraph").append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom);

    var zoom = d3.behavior.zoom().on("zoom", function() {
        g.attr("transform", "translate(" + d3.event.translate + ")" + " scale(" + d3.event.scale + ")");
    });

    var g = svg.append("g");


    svg.call(zoom);

    var link = g.selectAll(".link");
    var node = g.selectAll(".node").attr("cx", function(d) {
            return d.x;
        })
        .attr("cy", function(d) {
            return d.y;
        });

    javadocEntities
        .fetch()
        .then(function() {
            $scope.error = false;
            root = javadocEntities.getTree();
            update();
        }, function() {
            $scope.error = true;
        });

    function update() {

        var nodes = flatten(root),
            links = d3.layout.tree().links(nodes);

        force
            .nodes(nodes)
            .links(links);

        link = link.data(links, function(d) {
            return d.target.id;
        });

        link.exit().remove();

        link.enter().insert("line", ".node")
            .attr("class", "link");

        node = node.data(nodes, function(d) {
            return d.id;
        });

        node.exit().remove();

        var nodeEnter = node.enter().append("g")
            .attr("class", cssClass)
            .classed("node", true)
            .on("click", null)
            .on("click", click);

        nodeEnter.append("circle")
            .attr("r", function(d) {
                return 1 / d.level * 50 || 7;
            });

        nodeEnter.append("text")
            .text(getText)
            .classed("type", true)
            .attr("dy", "0.3em");

        nodeEnter.append("text")
            .attr("dy", "0.35em")
            .attr("dx", function(d) {
                return (d.level ? (1 / d.level * 50 * 0.1 + 0.35) : 0.75) + "em"
            })
            .text(function(d) {
                return qName(d.name);
            });


        var alphaMax = alpha = 1;
        var alphaMin = .3;

        function tick() {
            force.start();
            force.alpha(alpha);
            force.tick();
            alpha = force.alpha();
            force.stop();
            if (alpha >= alphaMin) {
                AJS.progressBars.update("#doc_structureGraphProgressbar", 1 - (alpha - alphaMin) / (alphaMax - alphaMin));
                setTimeout(tick, 0);
            } else {
                AJS.progressBars.update("#doc_structureGraphProgressbar", 1);
                setTimeout(function() {
                    $scope.loading = false;
                    $scope.loadingTakesLonger = false;
                    $timeout();
                }, 10);
            }

        }

        $timeout(tick);
        $timeout(function() {
            if ($scope.loading) {
                $scope.loadingTakesLonger = true;
            }
        }, 5000);

    }

    function tick() {
        link.attr("x1", function(d) {
                return d.source.x;
            })
            .attr("y1", function(d) {
                return d.source.y;
            })
            .attr("x2", function(d) {
                return d.target.x;
            })
            .attr("y2", function(d) {
                return d.target.y;
            });

        node.attr("transform", function(d) {
            return "translate(" + d.x + "," + d.y + ")";
        });
    }

    function getText(d) {
        if (d.type) {
            return d.type.charAt(0).toUpperCase();
        } else {
            return "";
        }
    }

    function cssClass(d) {
        var cl = "entityType" + $filter("capitalize")(d.type);
        if (d._children) {
            cl += " collapsed";
        } else if (d.children) {
            cl += " expanded";
        }
        return cl;
    }

    function click(d) {
        if (!d3.event.defaultPrevented) {
            if (typeof d.type == "undefined" || d.type === "package") {
                if (d.children) {
                    d._children = d.children;
                    d.children = null;
                } else {
                    d.children = d._children;
                    d._children = null;
                }
                update();
            } else {
                $state.go("javadoc.entity", {
                    name: d.name,
                    elementType: "classDiagram"
                });
            }
        }
    }

    function flatten(root) {
        var nodes = [], i = 0;

        function recurse(node) {
            if (node.children) node.children.forEach(recurse);
            if (!node.id) node.id = ++i;
            nodes.push(node);
        }

        recurse(root);
        return nodes;
    }
});