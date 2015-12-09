angular.module("DoC").filter('javadocMenuItemSorter', function() {

    return function(items, field) {
        var filtered = [];
        angular.forEach(items, function(item) {
            filtered.push(item);
        });

        filtered.sort(function (a, b) {
            if (a.type == b.type) {
                if (a.name == b.name) {
                    return 0;
                } else if (a.name> b.name ) {
                    return 1;
                } else {
                    return -1;
                }
            } else if (a.type > b.type) {
                return -1;
            } else {
                return 1;
            }

        });
        return filtered;
    };
});