Configuração Inicial

Criando uma Conta na HubSpot

Para utilizar a integração, é necessário criar uma conta na HubSpot. Dentro da plataforma, siga os seguintes passos:

Crie um novo aplicativo com o nome MyHubSpotAppTest.

Configure as seguintes autenticações:

URLs de redirecionamento:

https://hubspot-production-e626.up.railway.app

https://hubspot-production-e626.up.railway.app/auth/callback

Escopos Necessários:

crm.objects.contacts.read

crm.objects.contacts.write

Na seção de Webhook, adicione a seguinte URL:

https://hubspot-production-e626.up.railway.app/api/webhook

Em Assinaturas de eventos, configure:

Tipos de Objeto: Contato

Eventos a Monitorar: Criado

Após a configuração do aplicativo, crie uma conta de teste em TESTAR CONTAS.

Utilização da Aplicação

Acesse a URL onde a aplicação está hospedada:

https://hubspot-production-e626.up.railway.app

Preencha os campos CLIENT_ID e CLIENT_SECRET fornecidos pela HubSpot para prosseguir na autenticação.

Selecione a conta desejada e autorize o uso do aplicativo.

Após a autorização, você será redirecionado para a tela de cadastro de contatos.

Para cadastrar um contato:

Preencha todos os campos obrigatórios.

Clique em Salvar.

Uma mensagem de "Contato salvo" será exibida.

Após alguns segundos, o webhook retornará um JSON com os dados do evento de criação do contato.

Para adicionar mais contatos:

Preencha os dados novamente e clique em Salvar.

Caso o token expire, a aplicação possui uma função de renovação de token para continuar operando sem necessidade de nova autenticação manual.
