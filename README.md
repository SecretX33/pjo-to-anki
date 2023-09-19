# PJO to Anki

## Descrição

O PJO to Anki é uma extensão de navegador open-source que visa facilitar a vida dos estudantes que utilizam o site "PJO" para aprender e praticar frases. Com essa extensão, você pode automatizar a criação de cartas no programa Anki a partir das frases estudadas no PJO, economizando tempo e esforço.

### Disclaimer

Esta extensão **não** é oficial do PJO, e portanto não possui nenhum vínculo com PJO. O *PJO to Anki* é um projeto desenvolvido e mantido pela comunidade para melhorar a experiência de estudo dos usuários do PJO. Nenhum suporte ou garantia é fornecido pelo PJO em relação a esta extensão. **Por favor não mandem mensagem para o suporte do site à respeito de problemas na extensão,** ao invés disso, abram uma [Issue](https://github.com/SecretX33/pjo-to-anki/issues) diretamente aqui no GitHub. 

# Instalação

Para utilizar o **PJO to Anki**, siga as instruções abaixo:

1. Instale a extensão em seu navegador pela loja de extensões do seu navegador.

2. **[Importante!]** Siga as instruções da seção de [Configuração](#Configuração) *exatamente* como descritas.

3. Abra o Anki e mantenha ele aberto (necessário para que a extensão consiga criar as cartas no Anki).

4. Acesse o site do PJO e comece a estudar frases.

5. Quando encontrar uma frase que deseja adicionar ao Anki, clique no botão da extensão <img src="docs/icons/plus_icon.png" width="14" title="Botão de adicionar frase no Anki"> que estará ao lado da frase.

Pronto, agora você já consegue adicionar cartas ao Anki com um único clique!

# Configuração

Para configurar a extensão, siga os passos abaixo.

## 1. No Anki

Para que a extensão funcione, é necessário que você instale o addon [AnkiConnect](https://ankiweb.net/shared/info/2055492159) no seu Anki. Se você já possui esse addon no seu Anki, siga para a seção de [No Navegador](#2-no-navegador).

### Instalando o AnkiConnect

1. Na barra de menu do Anki, vá em "Ferramentas" > "Extensões", clique no botão "Obter extensões...", uma tela com um campo de "Código" será aberta para você.
2. Acesse a página oficial da extensão [AnkiConnect](https://ankiweb.net/shared/info/2055492159), e copie o código da extensão como indicado no site, será uma sequência númerica tal como `2055698787`.
3. No Anki, cole esse código no campo de "Código", pressione o botão de "OK". Quando a instalação do AnkiConnect finalizar, feche o Anki completamente, o abra novamente e continue seguindo o guia.

## 2. No navegador

1. Clique no ícone da extensão, e depois em "Configurações".

2. Na seção de configuração, insira as informações necessárias para a integração com o Anki. Segue uma breve descrição de cada item.
   1. **URL do Anki Connect:** Opção para usuários avançados, só será necessário alterar se você estiver usando configurações diferentes das desse guia no seu Anki Connect.
   2. **Nome do deck:** O nome do seu deck no Anki onde as cartas serão criadas.
   3. **Tipo de nota:** O nome do tipo de nota (modelo) de carta a ser usado para criar as cartas. Se você não sabe qual é o seu, abra seu Anki, vá em "Painel", selecione o deck desejado, e verifique o nome do modelo na coluna de **Nota**. 
   4. **Nome do campo da frente (do tipo de nota):** O nome do campo da frente da carta no tipo de nota escolhido. Se você não sabe qual é, abra seu Anki, vá em "Painel", selecione o deck desejado, selecione alguma carta, e verifique o que está escrito em cima da frase da frente da carta.
   5. **Nome do campo do verso (do tipo de nota):** O nome do campo do verso da carta no tipo de nota escolhido. Se você não sabe qual é, abra seu Anki, vá em "Painel", selecione o deck desejado, selecione alguma carta, e verifique o que está escrito em cima da frase do verso da carta.
   6. **Checar por cartas duplicadas em subdecks:** Opção para usuários avançados, não é necessário alterar.
   
3. Quando finalizar as customizações, salve as configurações.

Agora você já está pronto para utilizar a extensão e criar suas cartas.

## Licença

Este projeto é licenciado sob os termos da [Licença Pública Geral GNU, versão 3.0 (GPL-3.0)](https://www.gnu.org/licenses/gpl-3.0.en.html). Consulte o arquivo [LICENSE](LICENSE) para obter mais detalhes sobre os termos da licença.

Em poucas palavras, isso significa que esse projeto é gratuito e open-source, e que quaisquer derivados também serão gratuítos e open-source.