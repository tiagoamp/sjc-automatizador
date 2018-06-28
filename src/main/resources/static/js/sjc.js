var hostname = window.location.hostname;
var port = window.location.port;

$(document).ready(function () {
    
    $("#div-result").hide();
    $(".loader").hide();

    $("#btn-clean-upload").on('click', function (e) {
        $("#div-result").hide();
        cleanUploads();        
	});
	
    $("#btn-processar").on('click', function (e) {        
        $("#div-result").hide();
        $(".loader").show();
        process();
        $(this).attr("disabled",true);        
        $(".loader").hide();
    });    
    
    $("#btn-gerar-saida").on('click', function (e) {
        $(".loader").show();
        generateOutputSpreadsheet();
    });

    $("#btn-salvar-pdf").on('click', function (e) {
        $(".loader").show();
        generateOutputMessageFile();
    });    
    					
});

function cleanUploads() {
    $.ajax({
        url:"http://" + hostname + ":" + port + "/sjc/upload",
        type:"DELETE",
        success: function (data){
            showSuccessMessage("Arquivos do diretório apagados!");            
            window.location.reload(false);
        },
        error: function (data){
            showErrorMessage("Falha ao deletar arquivos!") ;        
        }
  });
}

function process() {
    getNumberOfUploadedSpreadsheets( loadSpreadsheets );    
}

function getNumberOfUploadedSpreadsheets( callback ) {
    $.get("http://" + hostname + ":" + port + "/sjc/upload/total", function( data ) {
        if (data == 0) {
            showErrorMessage("Não foram encontrados arquivos no diretório de uploads!");
            $("#btn-processar").attr("disabled",false);
            return;
        }
        $("#div-result").show();
        callback(data);
	})
    .fail( function() { showErrorMessage("Erro durante acesso ao sistema.") } );
}

function loadSpreadsheets(total) {
    for (i=0; i < total; i++) {
        loadSpreadsheetAtIndex(i);
    }            
}

function loadSpreadsheetAtIndex( i ) {
    var input = { index: i };
	
	$.get("http://" + hostname + ":" + port + "/sjc/input", input, function( data ) {
		if (data == null) { 
            showErrorMessage("Erro ao acessar o arquivo nro " + i + ".");
            return;
        }

        const isPlanilhaDeAfastamento = data.fileName.toLowerCase().includes("afastamento");
        if ( isPlanilhaDeAfastamento ) {
            showInfoMessage("Planilha de Afastamentos identificada: " + data.fileName);
        } else {
            showSuccessMessage("Planilha processada: " + data.fileName); 
        }

        createMessagesPanels(data);
	})
	.fail( function() { showErrorMessage("Falha no acesso ao arquivo (Índice = " + i + ").") } );
}

function createMessagesPanels( file ) {
    if (file.messages == null) return;
    createErrorPanel(file);    
    createAlertPanel(file);    
}

function createErrorPanel( file ) {
    var msgs = file.messages;
    var msgsErrorsUl = $("<ul>").addClass("text-danger");

    for (i=0; i < msgs.length; i++) {
        if (msgs[i].type == "ERROR") {
            msgsErrorsUl.append( $("<li>").text(msgs[i].text) );
        }        
    }

    if (msgsErrorsUl.children().length > 0) {
        var panelErrorDiv = $("<div>").addClass("panel panel-danger");
        var headErrorDiv = $("<div>").addClass("panel-heading");
        headErrorDiv.text(file.fileName);
        var bodyErrorDiv = $("<div>").addClass("panel-body");
        
        bodyErrorDiv.append(msgsErrorsUl);
        panelErrorDiv.append(headErrorDiv);
        panelErrorDiv.append(bodyErrorDiv);    
        $("#div-panel-errors").append(panelErrorDiv);
    }
}

function createAlertPanel( file ) {
    var msgs = file.messages;
    var msgsWarnsUl = $("<ul>").addClass("text-warning");
    
    for (i=0; i < msgs.length; i++) {
        if (msgs[i].type == "ALERT") {
            msgsWarnsUl.append( $("<li>").text(msgs[i].text) );
        }
    }

    if (msgsWarnsUl.children().length > 0) {
        var panelWarnDiv = $("<div>").addClass("panel panel-warning");
        var headWarnDiv = $("<div>").addClass("panel-heading");
        headWarnDiv.text(file.fileName);
        var bodyWarnDiv = $("<div>").addClass("panel-body");
    
        bodyWarnDiv.append(msgsWarnsUl);
        panelWarnDiv.append(headWarnDiv);
        panelWarnDiv.append(bodyWarnDiv);    
        $("#div-panel-warnings").append(panelWarnDiv);
    }
}

function generateOutputSpreadsheet() {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://' + hostname + ':' + port + '/sjc/output', true);
    xhr.responseType = 'arraybuffer';
    xhr.onload = function(e) {
       if (this.status == 200) {
          var blob=new Blob([this.response], {type:"application/vnd.ms-excel"});
          var link=document.createElement('a');
          link.href=window.URL.createObjectURL(blob);
          link.download="saida.xls";
          link.click();  
          $(".loader").hide();        
       }       
    };
    xhr.send();
}

function generateOutputMessageFile() {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://' + hostname + ':' + port + '/sjc/output/messages', true);
    xhr.responseType = 'arraybuffer';
    xhr.onload = function(e) {
       if (this.status == 200) {
          var blob=new Blob([this.response], {type:"application/pdf"});
          var link=document.createElement('a');
          link.href=window.URL.createObjectURL(blob);
          link.download="mensagens.pdf";
          link.click(); 
          $(".loader").hide();         
       }
    };
    xhr.send();
}


function showErrorMessage(msg) {
	return new PNotify({
        title: 'Erro!',
        text: msg,
        type: 'error',
        styling: 'bootstrap3'
    });
};

function showSuccessMessage(msg) {
	return new PNotify({
        title: 'Sucesso!',
        text: msg,
        type: 'success',
        styling: 'bootstrap3'
    });
};

function showInfoMessage(msg) {
	return new PNotify({
		title: 'Info',
        text: msg,
        type: 'info',
        styling: 'bootstrap3'
    });
};
