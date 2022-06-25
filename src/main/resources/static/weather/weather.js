angular.module('weather-data-collector').controller('weatherController', function ($scope, $http) {
    const contextPath = 'http://localhost:8000/weather/';
    $scope.weather = null;
    $scope.city = null;
    $scope.newCity = null;
    $scope.forecastCity = null;
    $scope.forecastCityName = null;
    $scope.forecastCountryName = null;
    $scope.deleteResponse = null;
    $scope.forecast = null;
    $scope.loadSavedWeather = function () {
        $http({
            url: contextPath + 'api/v1/all_saved',
            method: 'GET'
        }).then(function (response) {
            $scope.savedList = response.data;
        });
    };

    $scope.loadCurrentCity = function (){
        $http({
            url: contextPath + 'api/v1/city',
            method: 'GET'
        }).then(function (response){
            $scope.city = response.data;
        });
    }

    $scope.getSelectedWeather = function (id) {
        $http.get(contextPath + 'api/v1/' + id)
            .then(function (response) {
                $scope.weather = response.data;
            });
    }

    $scope.deleteSelectedWeather = function (id){
        if (confirm('Are you sure? ' + id + ' wil be deleted.')){
            $http({
                url: contextPath + 'api/v1/' + id,
                method: 'DELETE'
            }).then(function (response){
                $scope.deleteResponse = response.data;
                $scope.loadSavedWeather();
            });
        }
    }

    $scope.showWeatherItem = function () {
        return $scope.weather !== null;
    }

    $scope.changeCityName = function (){
        $http({
            url: contextPath + 'api/v1/city',
            method: 'POST',
            data: $scope.newCity
        }).then(function (response){
            $scope.city = response.data;
            $scope.loadSavedWeather();
            $scope.newCity = null;
        });
    }

    $scope.loadForecast = function (forecastCity) {
        $http({
            url: contextPath + 'api/v1/forecast/' + forecastCity,
            method: 'GET'
        }).then(function (response) {
            if (response.data.location !== null){
                $scope.forecastList = response.data.forecast.forecastday;
                $scope.forecastCityName = response.data.location.name;
                $scope.forecastCountryName = response.data.location.country;
                $scope.forecastError = null;
            }else {
                $scope.forecast = null;
                $scope.forecastCityName = null;
                $scope.forecastCountryName = null;
                $scope.forecastError = response.data.forecast.forecastday;
                $scope.forecastList = null;
            }
        });
    };

    $scope.getSelectedForecast = function (forecast){
        $scope.forecast = forecast;
    };

    $scope.loadSavedWeather();
    $scope.loadCurrentCity();
});