$(document).ready(function () {
  function clearModifyErrors() {
    $("#modify_title_error").addClass("d-none").text("");
    $("#modify_content_error").addClass("d-none").text("");
  }

  function showModifyErrors(errors) {
    clearModifyErrors();
    if (errors.title) {
      $("#modify_title_error").removeClass("d-none").text(errors.title);
    }
    if (errors.content) {
      $("#modify_content_error").removeClass("d-none").text(errors.content);
    }
  }

  function clearCommentErrors() {
    $("#comment_user_error").addClass("d-none").text("");
    $("#comment_text_error").addClass("d-none").text("");
  }

  function showCommentErrors(errors) {
    clearCommentErrors();
    if (errors.user) {
      $("#comment_user_error").removeClass("d-none").text(errors.user);
    }
    if (errors.content) {
      $("#comment_text_error").removeClass("d-none").text(errors.content);
    }
  }

  const postId = $("#detail_post_id").attr("value");
  console.log("postId - " + postId);

  $.ajax({
    url: "/post?id=" + postId,
  }).then(
    function (data) {
      console.log(data);
      $("#detail_title").text(data.title);
      $("#detail_user").text(data.user);
      $("#modify_user_text").val(data.user);
      $("#detail_date").text(data.updtDate);
      $("#detail_content").text(data.content);
    },
    function (err) {
      console.log(err.responseJSON);
    },
  );

  $.ajax({
    url: "/comments?post_id=" + postId,
  }).then(
    function (data) {
      $.each(data, function (index, e) {
        $("#comments").append(
          '<div class="media mb-4"><div class="media-body"><h5 class="mt-0">' +
            e.user +
            "</h5>" +
            e.content +
            "</div></div>",
        );
      });
      console.log(data);
    },
    function (err) {
      console.log(err.responseJSON);
    },
  );

  $("#detail_delete_btn").click(function () {
    const postId = $("#detail_post_id").attr("value");
    const confirmDelete = globalThis.confirm(
      "Apakah Anda yakin ingin menghapus post ini?",
    );
    if (!confirmDelete) {
      return;
    }

    console.log("delete button click! - " + postId);
    $.ajax({
      url: "/post?id=" + postId,
      method: "DELETE",
    }).then(
      function (data) {
        globalThis.location.href = "/";
      },
      function (err) {
        alert(err.responseJSON);
      },
    );
  });

  $("#modify_post_btn").click(function () {
    clearModifyErrors();
    const postId = $("#detail_post_id").attr("value");
    const user = $("#modify_user_text").val();
    const title = $("#modify_title_text").val();
    const content = $("#modify_content_text").val();

    console.log(postId);
    console.log(user);
    console.log(title);
    console.log(content);

    const param = {
      id: postId,
      user: user,
      title: title,
      content: content,
    };

    $.ajax({
      url: "/post",
      method: "PUT",
      dataType: "json",
      contentType: "application/json",
      data: JSON.stringify(param),
    }).then(
      function (data) {
        globalThis.location.href = "/page/detail/" + postId;
      },
      function (err) {
        if (err.responseJSON?.errors) {
          showModifyErrors(err.responseJSON.errors);
          return;
        }
        alert(err.responseJSON?.message ?? "Validation failed");
      },
    );
  });

  $("#create_comment_btn").click(function () {
    clearCommentErrors();
    const postId = $("#detail_post_id").attr("value");
    const user = $("#comment_user_text").val().trim();
    const commentText = $("#comment_text").val().trim();

    // Client-side quick validation to avoid sending empty fields
    const clientErrors = {};
    if (!user) clientErrors.user = "User tidak boleh kosong";
    if (!commentText) clientErrors.content = "Comment tidak boleh kosong";
    if (Object.keys(clientErrors).length > 0) {
      showCommentErrors(clientErrors);
      return;
    }

    const param = {
      postId: postId,
      user: user,
      content: commentText, // backend expects 'content'
    };

    $.ajax({
      url: "/comment",
      method: "POST",
      dataType: "json",
      contentType: "application/json",
      data: JSON.stringify(param),
    }).then(
      function (data) {
        globalThis.location.href = "/page/detail/" + postId;
      },
      function (err) {
        if (err.responseJSON?.errors) {
          showCommentErrors(err.responseJSON.errors);
          return;
        }
        alert(err.responseJSON?.message ?? "Validation failed");
      },
    );
  });
});
