$(document).ready(function () {
    
    $("#div-result").hide();

    $("#btn-clean-upload").on('click', function (e) {
        $("#div-result").hide();
        cleanUploads();        
	});
	
    $("#btn-processar").on('click', function (e) {        
        $("#div-result").hide();
        process();
        $(this).attr("disabled",true);
    });    
    
    $("#btn-salvar-saida").on('click', function (e) {        
        generateOutputSpreadsheet();
    });
    					
});

function cleanUploads() {
    $.ajax({
        url:"http://localhost:8080/sjc/upload",
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
    $.get("http://localhost:8080/sjc/upload/total", function( data ) {
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
	
	$.get("http://localhost:8080/sjc/input", input, function( data ) {
		if (data == null) { 
            showErrorMessage("Erro ao acessar o arquivo nro " + i + ".");
            return;
        }
        showSuccessMessage("Planilha processada: " + data.fileName);                
        createMessagesPanels(data);
	})
	.fail( function() { showErrorMessage("Falha no acesso ao arquivo.") } );
}

function createMessagesPanels( file ) {
    var msgs = file.messages;
    if (msgs == null) return;

    // ERRORS
    var msgsErrorsUl = $("<ul>").addClass("text-danger");

    var count = 0;
    for (i=0; i < msgs.length; i++) {
        if (msgs[i].type == "ERROR") {
            msgsErrorsUl.append( $("<li>").text(msgs[i].text) );
            count++;
        }        
    }
   
    if (count > 0) {
        var panelErrorDiv = $("<div>").addClass("panel panel-danger");
        var headErrorDiv = $("<div>").addClass("panel-heading");
        headErrorDiv.text(file.fileName);
        var bodyErrorDiv = $("<div>").addClass("panel-body");
        
        bodyErrorDiv.append(msgsErrorsUl);
        panelErrorDiv.append(headErrorDiv);
        panelErrorDiv.append(bodyErrorDiv);    
        $("#div-panel-errors").append(panelErrorDiv);
    }

    // WARNINGS
    var msgsWarnsUl = $("<ul>").addClass("text-warning");

    count = 0;
    for (i=0; i < msgs.length; i++) {
        if (msgs[i].type == "ALERT") {
            msgsWarnsUl.append( $("<li>").text(msgs[i].text) );
            count++;
        }        
    }
   
    if (count > 0) {
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

    console.log ("Gerando saída...");

    $.get("http://localhost:8080/sjc/output", function( data ) {
		if (data == null) { 
            showErrorMessage("Erro ao acessar o arquivo nro " + i + ".");
            return;
        }
        showSuccessMessage("Download!!");
	})
    .fail( function() { showErrorMessage("Falha no download do arquivo.") } );
    


    // clean upload directory at the end

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
