<div align="center">
<h1><img src="docs/icons/project_banner.png" width="350" alt="PJO to Anki Logo"></h1>
<p>Não perca mais tempo copiando e colando textos. Foque-se somente em entender a lição, e deixe a criação das cartas com o PJO to Anki.</p>

<a href="https://chrome.google.com/webstore/detail/pjo-to-anki/jpmfnecphnpnbongaehddmdchmodcilo"><img src="docs/icons/get_extension_chrome.png" alt="Get PJO to Anki on Chromium based browsers" height="55px"></a>
<a href="https://addons.mozilla.org/en-US/firefox/addon/pjo-to-anki"><img src="docs/icons/get_extension_firefox.png" alt="Get PJO to Anki for Firefox" height="55px"></a>
</div>

## 🎯 O que essa extensão faz?

Esta extensão automatiza a criação de cartas no Anki a partir das frases do site PJO. Com apenas um clique no botão que aparece ao lado das frases, a carta será automaticamente adicionada ao seu Anki, eliminando por completo a necessidade de copiar e colar manualmente o conteúdo do site no Anki.

## ⚠️ Disclaimer

Esta extensão não é oficial do PJO, e portanto não possui nenhum vínculo com PJO. O PJO to Anki é um projeto desenvolvido e mantido pela comunidade para melhorar a experiência de estudo dos usuários do PJO.

Nenhum suporte ou garantia é fornecido pelo PJO em relação a esta extensão. **Por favor não mandem mensagem para o suporte do site à respeito de problemas na extensão,** ao invés disso, abram uma [Issue](https://github.com/SecretX33/pjo-to-anki/issues) diretamente aqui no GitHub.

# 🚀 Instalação

1. Instale a extensão pela loja do seu navegador (links no topo deste readme)
2. Siga as instruções da seção de [Configuração](#configuração) *exatamente* como descritas
3. Mantenha o Anki aberto (necessário para a extensão criar as cartas)

Pronto! Agora você pode adicionar cartas ao Anki com um único clique no botão <img src="docs/icons/plus_icon.png" width="14" alt="Botão de adicionar frase no Anki"> ao lado das frases nas lições do site do PJO.

# ⚙️ Configuração

Para configurar a extensão, siga os passos abaixo.

## 1. No Anki

Para que a extensão funcione, é necessário que você instale o addon [AnkiConnect](https://ankiweb.net/shared/info/2055492159) no seu Anki. Se você já possui esse addon no seu Anki, siga para a seção de [No Navegador](#2-no-navegador).

### Instalando o AnkiConnect

O AnkiConnect é um addon para o Anki que permite que a extensão se comunique com o Anki para que seja possível adicionar novas cartas.

1. Na barra de menu do Anki, vá em "Ferramentas" > "Extensões", clique no botão "Obter extensões...", uma tela com um campo de "Código" será aberta para você.
2. Acesse a página oficial da extensão [AnkiConnect](https://ankiweb.net/shared/info/2055492159), e copie o código da extensão como indicado no site, será uma sequência númerica tal como `2055492159`.
3. No Anki, cole esse código no campo de "Código", pressione o botão de "OK". Quando a instalação do AnkiConnect finalizar, feche o Anki completamente, o abra novamente e continue seguindo o guia.

## 2. No navegador

1. Clique no ícone da extensão, e depois em "Configurações".

2. Na seção de configuração, insira as informações necessárias para a integração com o Anki. Segue uma breve descrição de cada item.
    1. **URL do Anki Connect:** Opção para usuários avançados, só será necessário alterar se você estiver usando configurações diferentes das desse guia no seu Anki Connect.
    2. **Nome do deck:** O nome do seu deck no Anki onde as cartas serão criadas.
    3. **Tipo de nota:** O nome do tipo de nota (modelo) de carta a ser usado para criar as cartas. Se você não sabe qual é o seu, abra seu Anki, vá em "Painel", selecione o deck desejado, e verifique o nome do modelo na coluna de **Nota**.
    4. **Nome do campo da frente (do tipo de nota):** O nome do campo da frente da carta no tipo de nota escolhido. Se você não sabe qual é, abra seu Anki, vá em "Painel", selecione o deck desejado, selecione alguma carta, e verifique o que está escrito em cima da frase da frente da carta.
    5. **Nome do campo do verso (do tipo de nota):** O nome do campo do verso da carta no tipo de nota escolhido. Se você não sabe qual é, abra seu Anki, vá em "Painel", selecione o deck desejado, selecione alguma carta, e verifique o que está escrito em cima da frase do verso da carta.
    6. **Nome do campo de áudio:** (opcional) O nome do campo de áudio do seu tipo de nota escolhido, se você quiser adicionar os áudios das frases, ou deixe em branco para não adicionar áudios.
        1. Note que, por padrão, o modelo de carta do Anki **não vem** com um campo para áudio, sendo necessário que você edite o seu modelo de carta para adicionar esse campo. Se você tiver dificuldades em fazer isso, peça ajuda no Discord do PJO (comunidade), ou entre em contato direto comigo pelo Discord que eu te ajudo.
    7. **Checar por cartas duplicadas em subdecks:** Opção para usuários avançados, não é necessário alterar.

3. Quando finalizar as customizações, salve as configurações.

Agora você já está pronto para utilizar a extensão e criar suas cartas.

## 📝 Licença

Este projeto é licenciado sob os termos da [Licença Pública Geral GNU, versão 3.0 (GPL-3.0)](https://www.gnu.org/licenses/gpl-3.0.en.html). Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

> 💡 Em resumo: o projeto é gratuito e open-source, e qualquer trabalho derivado também deve ser gratuito e open-source.
