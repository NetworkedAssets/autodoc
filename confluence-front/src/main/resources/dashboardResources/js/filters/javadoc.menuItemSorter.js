angular.module("DoC").filter('javadocMenuItemSorter', function() {

    function order() {

    }

    return function(items, field) {
        var filtered = [];
        angular.forEach(items, function(item) {
            filtered.push(item);
        });

        filtered.sort(function (a, b) {
            //console.log(a.type, b.type);
            if (a.type == b.type) {
                return a.name < b.name;
            } else {
                //console.log(a.name,b.name);
                return a.type >= b.type;
            }
        });
        return filtered;
    };
});