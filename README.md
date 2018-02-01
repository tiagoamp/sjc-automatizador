# sjc-automatizador

* Portguese
* English

<i> -- -- -- --------- -- -- --</i>

<i> -- -- -- [ PT-BR ] -- -- --</i>

<b> Descrição </b>

Sistema para automatização da leitura e validação de planilhas pela SJC, gerando uma única planilha com a junção das entradas em layout/formato pré-definido.

<b> Processo de Negócio </b>

São enviadas semanalmente centenas de planilhas Excel (xlsx) em determinado layout/formatação, com abas pré-definidas, e a planilha resultante também deve atender a um layout/formatação especificada.

O fluxo de processo é algo assim:

    1. Carregar planilhas do diretório 
    2. Ler e validar os dados de cada planilha 
    3. Gerar relatório 'html' com as mensagens do processamento (log)
    4. Gerar planilha de saída


<b> Arquitetura e Tecnologia </b>

Aplicação implementada em linguagem Java, com interface usando html/javascript acessando serviços REST.
Utilizada bibliotecas 'apache poi' para manipulação das planilhas Excel.
Utiliza Spring Boot para implementação web, rodando servidor local.

<b> Por que publicado no Github? </b>

O sistema foi publicado no github para propiciar o conhecimento dos fontes pela empresa que faz uso do sistema, bem como viabilizar sua manutenção por terceiros se necessário.
O projeto é aberto e não contém informaçes confidenciais no código-fonte.


<i> -- -- -- --------- -- -- --</i>

<i> -- -- -- [ ENG ] -- -- --</i>

Application builded to the SJC Company to automate the work of loading and validating Excel spreadsheets, merging them and generating one output spreadsheet with joined data.


<b> Workflow </b>

It is received hundreds of Excel spreadsheet weekly in a given layout and given sheets names, and the output spreadsheet must follow a specified layout too.

The workflow is like this:

    1. Load spreeadsheets files from directory
    2. Read and validate sheets data
    3. Generate 'html' report containing the processing messages/log
    4. Generate output spreadsheet


<b> Architecture and Technology </b>

Application built in Java, UI build with html/javascript accessing REST services.
It was used 'apache poi' library to handle Excel spreadsheets.
Uses Spring Boot for this web version, running on local server.

<b> Why is it published in Github? </b>

The application was published in github to help the company to get knowledge in its source code, since it can be sustained by others developers if it was necessary. 
It is an open project and there are not confidential information in this source code.


There are some known <b> TO DOs </b> that will be included in this git project (issues).

That's it, I guess...
