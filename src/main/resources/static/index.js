(function () {
    angular
        .module('weather-data-collector', ['ngRoute', 'ngStorage'])
        .config(config)
        .run(run);

    function config($routeProvider) {
        $routeProvider
            .when('/welcome', {
                templateUrl: 'welcome/welcome.html',
                controller: 'welcomeController'
            })
            .when('/weather', {
                templateUrl: 'weather/weather.html',
                controller: 'weatherController'
            })
            .when('/contacts', {
                templateUrl: 'contacts/contacts.html',
                controller: 'contactsController'
            })
            .when('/blog', {
                templateUrl: 'blog/blog.html',
                controller: 'blogController'
            })
            .when('/orders', {
                templateUrl: 'order/orders.html',
                controller: 'ordersController'
            })
            .when('/order_pay/:orderId', {
                templateUrl: 'order_pay/order_pay.html',
                controller: 'orderPayController'
            })
            .when('/registration', {
                templateUrl: 'registration/registration.html',
                controller: 'registrationController'
            })
            .when('/admin', {
                templateUrl: 'admin/admin.html',
                controller: 'adminController'
            })
            .when('/product_form/\\w+', {
                templateUrl: 'product/product_form.html',
                controller: 'productFormController'
            })
            .otherwise({
                redirectTo: '/'
            });
    }

    function run($rootScope, $http, $localStorage) {
    }
})();

angular.module('weather-data-collector').controller('indexController', function ($rootScope, $scope, $http, $location, $localStorage) {
});