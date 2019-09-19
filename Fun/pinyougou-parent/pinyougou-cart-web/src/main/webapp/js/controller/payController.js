app.controller('payController', function ($scope, payService,$location,loginService) {

    // 获取当前登录的用户名
    $scope.showName = function () {
        loginService.showName().success(
            function (response) {
                $scope.username = response.loginName;
            }
        );
    };

    //本地生成二维码
    $scope.createNative = function () {
        payService.createNative().success(
            function (response) {
                $scope.money = (response.total_fee / 100).toFixed(2);	//金额
                $scope.out_trade_no = response.out_trade_no;//订单号

                // 二维码
                var qr = new QRious({
                    element: document.getElementById('qrious'),
                    size: 250,
                    level: 'H',
                    // 此处位置的内容需要未来更改之后，更新  response.code_url
                    value: www.baidu.com
                });

                $scope.queryPayStatus();//查询支付状态

            }
        );
    };

    //查询支付状态
    $scope.queryPayStatus = function () {
        payService.queryPayStatus($scope.out_trade_no).success(
            function (response) {
                if (response.success) {
                    location.href="paysuccess.html#?money="+$scope.money;
                } else {
                    if (response.message == '二维码支付超时') {
                        // 优化客户体验
                        $scope.overtimeMessage = '二维码已过期，刷新页面重新获取二维码';
                        // $scope.createNative();//重新生成二维码
                    } else {
                        location.href = "payfail.html";
                    }
                }
            }
        );
    };

    // 点击刷新二维码
    $scope.flashdQrCode = function () {
        $scope.createNative();//重新生成二维码
        $scope.overtimeMessage ='';  //点击后，更新二维码的提示信息为空
    }


    //获取金额
    $scope.getMoney=function(){
       $scope.payMoney  =  $location.search()['money'];
    }

});