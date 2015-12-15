/**
 * Created by Jakub on 30/11/15.
 */
angular.module("DoC_Config").controller("schedulesCtrl",function($scope,settingsData) {
    var schedules = this;
    schedules.types= [
        {
            label: "day",
            value: "day"
        },
        {
            label: "week",
            value: "week"
        }
    ];

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
        },
    };

    schedules.settings = settingsData;

    schedules.log = function() {
        console.log(schedules.settings.schedule);
    }

    schedules.removeEvent = function(id) {
        schedules.settings.scheduledEvents.splice(id,1);
    }

    schedules.addEvent = function() {
        schedules.settings.scheduledEvents.push(
            {
                "periodic": false,
                "type": "day",
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
    }



})
