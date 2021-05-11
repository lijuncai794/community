$(function () {
    $("#delPost").click(deletePost);
});

function like(btn, entityType, entityId, entityUserId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId": entityId, "entityUserId": entityUserId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) { //请求成功
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus == 1 ? '已赞' : "赞");
            } else if (data.code == 2){//未登录，无访问权限
                location.href = CONTEXT_PATH + "/login";
            } else { //请求失败
                alert(data.msg);
            }
        }
    );
}

//删除帖子
function deletePost() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg);
            }
        }
    );
}

//删除评论
function deleteComment(commentId) {
    $.post(
        CONTEXT_PATH + "/comment/delete",
        {"id": commentId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#" + commentId).hide();
            } else {
                alert(data.msg);
            }
        }
    );
}