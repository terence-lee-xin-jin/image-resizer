
"use strict";

/*
    Author: Terence Lee
*/

$(document).ready(()=>setSliderAndButtonEventHandler());


/*
    Set the event handlers for the various controls
*/
function setSliderAndButtonEventHandler(){

    //each time the slide is moved, update the percentage on the screen
    $("#reduce-image-size-by-percentage-slider").on("input", onSliderInput);

    //upon clicking on the resize button, make a request to the server to resize the image
    $("#resize-button").click(resizeImage);
}


/*
    The callback function when the slider is being dragged
    Updates the percentage value being displayed on screen
*/
function onSliderInput(){

    let rangeInput = $("#reduce-image-size-by-percentage-slider").val();

    $("#reduce-image-size-by-percentage-span").text(rangeInput + "%");
}


/*
    Validate whether the user has uploaded a valid image to be resized

    Displays relevant error messages if the image has not been selected/too large.

    Returns true if image is valid, otherwise returns false.
*/
function validateUploadedImage(){

    let filesArray = $("#image-upload-input").prop("files");

    const MAX_FILE_SIZE_IN_BYTES = 2000000;

    let errorMessage = "";
    let isImageUploadedValid = true;

    if(!filesArray || !filesArray[0]){

        isImageUploadedValid = false;
        errorMessage = "Image file is not selected";

    }else if (filesArray[0].size > MAX_FILE_SIZE_IN_BYTES){

        isImageUploadedValid = false;
        errorMessage = "File size exceed supported max size of 2MB"
    }


    if (!isImageUploadedValid){
        $("#image-upload-input-error-message").text(errorMessage);
        $("#image-upload-input-error-message").show();

    }else{
        $("#image-upload-input-error-message").hide();
    }

    return isImageUploadedValid;

}


/*
    Obtain the image uploaded by the user. Assumes that the user has
    already uploaded the image.

    Use validateUploadedImage() to check whether the user has uploaded a valid image first.
*/
function getUploadedImage(){

    let filesArray = $("#image-upload-input").prop("files");

    return filesArray[0];
}




/*
    Returns the percentage of image size to be reduced as an integer
*/
function getPercentageReductionOfImageSize(){

    let percentageReduction = $("#reduce-image-size-by-percentage-slider").val()
    percentageReduction = parseInt(percentageReduction);

    return percentageReduction;

}


/*
    This is the event handler for the event when the user clicks on the "Resize" button
*/
function resizeImage(){

    $("#resize-success-and-download-link-message").hide();

    let imageUploadedIsValid = validateUploadedImage();


    if (imageUploadedIsValid){

        let formData = new FormData();

        let uploadedImageFile = getUploadedImage();
        let percentageReductionOfImageSize = getPercentageReductionOfImageSize();

        formData.append("percentageReductionOfImageSize", percentageReductionOfImageSize);
        formData.append("image", uploadedImageFile);

        makeRequestToServer(formData)
    }

}


/*
    Make a HTTP request to the server to resize the image
*/
function makeRequestToServer(formData){

    displayWaitAMomentMessageAndHideExistingErrorMessage();

    var httpRequest = new XMLHttpRequest();

    httpRequest.responseType = "blob";
    httpRequest.onreadystatechange = function() {

        const HTTP_REQUEST_COMPLETE = 4;
        const OK_WITHOUT_ERROR_STATUS = 200;

        if (this.readyState == HTTP_REQUEST_COMPLETE){
            $("#please-wait-a-moment-message").hide();

            if (this.status == OK_WITHOUT_ERROR_STATUS){
                onImageResizeServerRequestSuccess(this.response);
            }
            else if(this.status != OK_WITHOUT_ERROR_STATUS){
                onImageResizeServerRequestError();
            }
        }

    };


    //set timeout when server fails to reply within designated time
    const TEN_SECONDS_IN_MILLISECONDS = 10000;
    httpRequest.timeout = TEN_SECONDS_IN_MILLISECONDS;
    httpRequest.ontimeout = onImageResizeServerRequestTimeout;


    const MAKE_REQUEST_ASYNC = true;
    httpRequest.open("POST", "./resize-image", MAKE_REQUEST_ASYNC);
    httpRequest.send(formData);

}


/*
    Displays a message asking the user to wait for a moment while making a request to server.
    Also, hides the error message from previous request if any
*/
function displayWaitAMomentMessageAndHideExistingErrorMessage(){

    $("#please-wait-a-moment-message").show();
    $("#resize-error-message").hide();
}



/*
    The callback function when the server request for resizing the image is success

    Does the following:
        (a) Sets the name of the original file name in the download message.
                (e.g. The image picture-1.png is ready for download)
        (b) Sets the name of the image to be downloaded (prepend a "resized-" to original image name)
        (c) Sets the download link for the resized image
        (d) Displays (Unhide) the download message and its download link
*/
function onImageResizeServerRequestSuccess(resizedImageBlob){

    //displays the original filename of the image file uploaded by the user
    let uploadedImageFileName = getUploadedImageFileName();
    $("#download-link-message-original-file-name").text(uploadedImageFileName);


    //sets the name of the re-sized image file by pre-pending the "resize-" string to
    // the original file name
    let resizeImageForDownloadFileName = "resized-" + uploadedImageFileName;
    $("#resized-image-download-link").prop("download", resizeImageForDownloadFileName);


    //creates the dynamic URL for downloading the resized image, and sets
    //the url for the download link
    let downloadURL = URL.createObjectURL(resizedImageBlob);
    $("#resized-image-download-link").attr("href",downloadURL );


    //shows the hidden download link message
    $("#resize-success-and-download-link-message").show();
}


/*
    Obtain the filename of the image uploaded by the user. Assumes that the user has
        already uploaded the image.

    Use validateUploadedImage() to check whether the user has uploaded a valid image first.
*/
function getUploadedImageFileName(){

    let filesArray = $("#image-upload-input").prop("files");

    return filesArray[0].name;
}


/*
    The callback function when the server request ends in an error. Displays an
    error message about the error
*/
function onImageResizeServerRequestError(){
    $("#resize-error-message").text("An error has occurred. Please try again later.");
    $("#resize-error-message").show();
}


/*
    The callback function when the server request is taking too long. Displays an
    error message about the server request being too long
*/
function onImageResizeServerRequestTimeout(){

    $("#resize-error-message").text("Server took too long to reply. Please try again later.");
    $("#resize-error-message").show();
}