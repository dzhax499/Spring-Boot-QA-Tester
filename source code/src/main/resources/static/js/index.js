$(document).ready(function () {
  function clearCreatePostErrors() {
    $("#create_user_error").addClass("d-none").text("");
    $("#create_title_error").addClass("d-none").text("");
    $("#create_content_error").addClass("d-none").text("");
  }

  function showCreatePostErrors(errors) {
    clearCreatePostErrors();
    if (errors.user) {
      $("#create_user_error").removeClass("d-none").text(errors.user);
    }
    if (errors.title) {
      $("#create_title_error").removeClass("d-none").text(errors.title);
    }
    if (errors.content) {
      $("#create_content_error").removeClass("d-none").text(errors.content);
    }
  }

  function validateCreatePostForm() {
    const errors = {};
    const user = $("#create_user_text").val().trim();
    const title = $("#create_title_text").val().trim();
    const content = $("#create_content_text").val().trim();

    if (!user) {
      errors.user = "User tidak boleh kosong";
    }
    if (!title) {
      errors.title = "Title tidak boleh kosong";
    }
    if (!content) {
      errors.content = "Content tidak boleh kosong";
    }

    if (Object.keys(errors).length > 0) {
      showCreatePostErrors(errors);
      return null;
    }

    return {
      user: user,
      title: title,
      content: content,
    };
  }

  $.ajax({
    url: "/posts",
  }).then(
    function (data) {
      $.each(data, function (index, e) {
        $("#posts").append(
          '<div class="card mb-4"> <div class="card-body"> <h2 class="card-title">' +
            e.title +
            '</h2> <p class="card-text">' +
            e.content +
            '</p> <a href="/page/detail/' +
            e.id +
            '" class="btn btn-primary">Read More &rarr;</a> </div> ' +
            '<div class="card-footer text-muted"> Posted on ' +
            e.updtDate +
            " by " +
            e.user +
            "</div> </div>",
        );
      });
      console.log(data);
    },
    function (err) {
      console.log(err.responseJSON);
    },
  );

  $("#create_post_form").on("submit", function (event) {
    event.preventDefault();
    clearCreatePostErrors();

    const param = validateCreatePostForm();
    if (!param) {
      return;
    }

    $.ajax({
      url: "/post",
      method: "POST",
      dataType: "json",
      contentType: "application/json",
      data: JSON.stringify(param),
    }).then(
      function () {
        globalThis.location.href = "/";
      },
      function (err) {
        if (err.responseJSON?.errors) {
          showCreatePostErrors(err.responseJSON.errors);
          return;
        }
        alert(err.responseJSON?.message ?? "Validation failed");
      },
    );
  });
});
