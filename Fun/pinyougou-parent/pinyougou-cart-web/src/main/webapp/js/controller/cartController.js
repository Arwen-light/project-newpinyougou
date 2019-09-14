app.controller("cartController", function ($scope, cartService, loginService) {


    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;

                $scope.total = cartService.sum(response);//求合计数
            });
    };

    //添加数量到购物车列表
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function (response) {
                if (response.success) {
                    $scope.findCartList();
                } else {
                    alert(response.message);  // 给出提示信息
                }
            }
        );
    };

    $scope.showName = function () {
        loginService.showName().success(
            function (response) {
                $scope.username = response.loginName;
            }
        );
    };

    //获取地址列表
    $scope.findAddressList = function () {
        cartService.findAddressList().success(
            function (response) {
                $scope.addressList = response;

                //设置默认地址
                for (var i = 0; i < $scope.addressList.length; i++) {
                    if ($scope.addressList[i].isDefault == '1') {
                        $scope.address = $scope.addressList[i];
                        break;
                    }
                }
            }
        );
    };

    //选择地址
    $scope.selectAddress = function (address) {
        $scope.address = address;
    };

    //判断是否是当前选中的地址
    $scope.isSelectedAddress = function (address) {
        if (address == $scope.address) {
            return true;
        } else {
            return false;
        }
    };

    $scope.order = {paymentType: '1'};
    //选择支付方式
    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    };
    // 提交订单
    $scope.submitOrder = function () {

        // order 中填充数据
        $scope.order.receiverAreaName =  $scope.address.address;
        $scope.order.receiverMobile =  $scope.address.mobile;
        $scope.order.receiver =  $scope.address.contact;

        cartService.submitOrder($scope.order).success(
            function (response) {
                if(response.success){
                    // 下单成功
                    if( $scope.order.paymentType == "1"  ) {  // 微信支付方式
                        location.href="pay.html";
                    }else{  // 线下支付
                        location.href="paysuccess.html"
                    }
                }else{
                    // 下单失败
                    alert("下单失败");
                    location.href = "payfail.html"
                }
            }
        )
    }

});