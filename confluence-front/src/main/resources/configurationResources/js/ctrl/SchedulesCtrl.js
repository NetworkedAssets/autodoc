angular.module("DoC_Config").controller("SchedulesCtrl",function($scope,settingsService) {
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

    schedules.settings = settingsService;

    schedules.log = function() {
        console.log(schedules.settings.schedule);
    };

    schedules.removeEvent = function(id) {
        schedules.settings.scheduledEvents.splice(id,1);
    };

    schedules.addEvent = function() {
        schedules.settings.scheduledEvents.push(
            {
                "periodic": false,
                "periodType": "DAY",
                "number": 1,
                "weekdays": {
                    "mon": false,
                    "tue": false,
                    "wed": true,
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

