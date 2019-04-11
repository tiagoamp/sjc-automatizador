const hostname = window.location.hostname;
const port = 8090 || window.location.port;

const URL_BASE = "http://" + hostname + ":" + port + "/sjc/";

const URL_UPLOAD_INPUT_FILE = URL_BASE + "upload2";
const URL_DELETE_AFAST = URL_BASE + "upload/afast";


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
    }


}

export default httpGatewayFunctions;