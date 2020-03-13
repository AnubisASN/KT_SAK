function base64() {
    var nameId = document.getElementById('name');
    var cardId = document.getElementById('card');
    var encodebefore = Base64.encode(nameId.value);

    nameId.value = encodebefore;
    document.getElementById('form_input_register').submit();
    nameId.value = "";
    cardId.value = "";
}

function getNewDate() {
    var date = new Date();
    console.log(date);
    var transverse = "-";
    var Verticalpoint = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    var strHour = date.getHours();
    var strMinute = date.getMinutes();
    var strSeconde = date.getSeconds();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 1 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    if (strHour >= 0 && strHour <= 9) {
        strHour = "0" + strHour
    }
    if (strMinute >= 0 && strMinute <= 9) {
        strMinute = "0" + strMinute;
    }

    if (strSeconde >= 0 && strSeconde <= 9) {
        strSeconde = "0" + strSeconde;
    }
    var NewDate = date.getFullYear() + transverse + month + transverse + strDate + " " +
        strHour + Verticalpoint + strMinute + Verticalpoint + strSeconde;
    console.log(NewDate);
    $("#time").val(NewDate);
}


function showPicture(imgFile) {
    document.getElementById("img").src = window.URL.createObjectURL(imgFile.files[0]);
}


function getSetting() {
    htmlobj = $.ajax({
        url: "getSet",
        success: function (result) {
            var jsonObj = JSON.parse(result);
            $("#serverIp").val(jsonObj.serviceIp);
            $("#serverPort").val(jsonObj.servicePort);
        }
    });
}

function getUpdate() {
    var formData = new FormData();
    var file = $("#upFile")[0].files[0];
    if (file === undefined) {
        var url = "getUpdate";
        alert("设备下载中,完成后自动更新重启");
    } else {
        var url = "updateAPP";
        formData.append("file", file);
        alert("上传中,请耐心等待");
    }
    $.ajax({
        url: url,
        data: formData,
        type: "POST",
        dataType: "json",
        contentType: false,
        processData: false,
        success: function () {
            alert("成功,设备更新中")
        }
    });

}


function setTime() {
    $.ajax({
        type: "POST",
        dataType: "json",
        contentType: "application/json",
        url: "tcpData",
        data: JSON.stringify({
            "executeId": "setTime-110",
            "deviceNum": "110",
            "msgType": "setTime",
            "dataInfo": {
                "extend": {
                    "time": $("#time").val()
                }
            }
        }),
        success: function (result) {
            if (result === "200")
                alert("成功");
            else
                alert("失败:" + result);
        }
    });
}


function setting() {
    $.ajax({
        type: "POST",
        dataType: "json",
        contentType: "application/json",
        url: "tcpData",
        data: JSON.stringify({
            "executeId": "setInfo-110",
            "deviceNum": "110",
            "msgType": "setInfo",
            "dataInfo": {
                "extend": {
                    "serviceIp": $("#serverIp").val(),
                    "servicePort": $("#serverPort").val()
                }
            }
        }),
        success: function (result) {
            if (result === "200")
                alert("成功");
            else
                alert("失败:" + result);
        }
    });
}


function tcpData() {
    $.ajax({
        type: "POST",
        dataType: "json",
        contentType: "application/json",
        url: "tcpData",
        data: $("#raw_data").val(),
        success: function (result) {
            if (result === "200")
                alert("成功");
            else
                alert("失败:" + result);
        }
    });
}

function projection() {
    var ua = navigator.userAgent.toLocaleLowerCase();
    if (!ua.match("chrome")) {
        alert("此浏览器不支持,请切换Google浏览器");
        return
    }
    window.location.href = 'Vysor.zip';
    alert("请在浏览器输入'chrome://extensions/',将下载的文件解压后拖放至浏览器界面中.插件安装成功后在地址输入'chrome://apps/',点击vysor启动");
}

function isExtension() {
    var extensionid = "kfdpcknamlankfapagpdempakklkdoim";
    var image = document.createElement('img');
    image.src = 'chrome-extension://' + extensionid + '/icon.png';
    console.log('chrome-extension://' + extensionid + '/icon.png');
    image.onload = function () {
        console.log("安装");
        return true
    };
    image.onerror = function () {
        console.log("未安装");
        return false
    };
}

function exec() {
    var cmd = "C:\Program Files (x86)GoogleChromeApplicationchrome_proxy.exe  --profile-directory=Default --app-id=kfdpcknamlankfapagpdempakklkdoim";
    var shell = new ActiveXObject("vysor");
    shell.run(cmd);

}
