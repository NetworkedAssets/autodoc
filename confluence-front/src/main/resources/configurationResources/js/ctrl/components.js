/**
 * Created by Jakub on 30/11/15.
 */
angular.module("DoC_Config").controller("componentsCtrl",function($scope,settingsData) {
    var components = this;

    components.available = [{
        "name": "javadoc",
        "displayName": "Javadoc"
    },{
        "name": "classDiagram",
        "displayName": "UML ClassDiagram"
    }];

    components.settings = settingsData;

    angular.element(document).ready(function () {
        $("#doc_config_components .toggle").on("change",function() {
            components.change($(this).data("name"),$(this).prop("checked"));
        });
    });

    components.change = function(component,value) {
        settings.components[component] = value;
        $scope.$apply();
    };

    components.log = function() {
        console.log(settings);
    }


});