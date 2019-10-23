$(document).ready(function () {
    $("#btnSubmit").click(function (event) {
        event.preventDefault();
        fire_ajax_submit();
    });
});

var myData = [
    {
        "email": "email@gmail.com",
        "pub_id": "cfd04fccdabdece15d7a"
    },
    {
        "email": "email@ukr.com",
        "pub_id": "fewfewf32ewf2v2cdscf"
    }
];

var textedJson = JSON.stringify(myData, undefined, 4);
$('#myTextarea').text(textedJson);

function fire_ajax_submit() {
    var form = $('#fileUploadForm')[0];
    var data = new FormData(form);
    $("#btnSubmit").prop("disabled", true);

    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/api/upload",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {
            $("#result").text(data);
            console.log("SUCCESS : ", data);
            $("#btnSubmit").prop("disabled", false);
        },
        error: function (e) {
            $("#result").text(e.responseText);
            console.log("ERROR : ", e);
            $("#btnSubmit").prop("disabled", false);
        }
    });

}
