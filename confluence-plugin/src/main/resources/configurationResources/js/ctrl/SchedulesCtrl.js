angular.module("DoC_Config").controller("SchedulesCtrl",function($scope) {
    var schedules = this;
    schedules.types = {
        singular: [
            {
                label: "day",
                value: "DAY"
            }, {
                label: "week",
                value: "WEEK"
            }
        ],
        plural: [
            {
                label: "days",
                value: "DAY"
            }, {
                label: "weeks",
                value: "WEEK"
            }
        ]
    };

    schedules.weekdayOptions = {
        "mon": {
            label: "Mon",
            value: "mon"
        },
        "tue": {
            label: "Tue",
            value: "tue"
        },
        "wed": {
            label: "Wed",
            value: "wed"
        },
        "thu": {
            label: "Thu",
            value: "thu"
        },
        "fri": {
            label: "Fri",
            value: "fri"
        },
        "sat": {
            label: "Sat",
            value: "sat"
        },
        "sun": {
            label: "Sun",
            value: "sun"
        }
    };

    var spaceTools = $scope.$parent.spaceTools;

    schedules.log = function() {
        console.log(spaceTools.schedule);
    };

    schedules.removeEvent = function(id) {
        spaceTools.scheduledEvents.splice(id,1);
    };

    schedules.addEvent = function() {
        spaceTools.scheduledEvents.push(
            {
                "periodic": false,
                "periodType": "DAY",
                "number": 1,
                "weekdays": {
                    "mon": false,
                    "tue": false,
                    "wed": false,
                    "thu": false,
                    "fri": false,
                    "sat": false,
                    "sun": false
                },
                "oneTimeDate": null,
                "time": "17:00"
            }
        );
    };

});

