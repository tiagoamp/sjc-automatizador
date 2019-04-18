const hostname = window.location.hostname;
const port = 8090 || window.location.port;

const URL_BASE = "http://" + hostname + ":" + port + "/sjc/";

const URL_UPLOAD_INPUT_FILE = URL_BASE + "upload2";
const URL_DELETE_AFAST = URL_BASE + "upload/afast";
const URL_CONVERT_INPUT_FILES = URL_BASE + "convert";
const URL_CONVERT_TOTAL_INPUT_FILES = URL_BASE + "convert/total";
const URL_PROCESS_INPUT_FILES = URL_BASE + "process";
const URL_DOWNLOAD_MSGS_FILE = URL_BASE + "messages";
const URL_DOWNLOAD_OUTPUT_FILE = URL_BASE + "output2";


const httpGatewayFunctions = {

    uploadFileRequest: (data) => {
        let formData  = new FormData();
        formData.append("inputfile", data);
        return fetch(URL_UPLOAD_INPUT_FILE, {
                method: 'POST',
                body: formData
            });        
    },

    cleanDirsRequest: () => {
        return fetch(URL_BASE, {method: 'DELETE'});
    },

    deleteAfastamentoRequest: () => {
        return fetch(URL_DELETE_AFAST, {method: 'DELETE'})
    },

    convertInputFiles: () => {
        return fetch(URL_CONVERT_INPUT_FILES);
    },

    totalConvertInputFiles: () => {
        return fetch(URL_CONVERT_TOTAL_INPUT_FILES);
    },

    processInputFiles: () => {
        return fetch(URL_PROCESS_INPUT_FILES);
    }, 

    downloadMessagesFile: () => {
        return fetch(URL_DOWNLOAD_MSGS_FILE);
    },

    downloadOutputFile: () => {
        return fetch(URL_DOWNLOAD_OUTPUT_FILE);
    }

}

export default httpGatewayFunctions;