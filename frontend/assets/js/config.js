// CONFIGURAÇÕES GLOBAIS DA APLICAÇÃO

const CONFIG = {
  // URL base do servidor Back-end Spring Boot
  API_BASE: "https://eq21.dsc.rodrigor.com",
  
  // Número do WhatsApp da Trokets 
  WHATSAPP_NUMERO: "5584996980815",
  
  // Mensagem padrão de saudação ao abrir o link
  WHATSAPP_SAUDACAO: "Olá! Gostaria de confirmar o meu pedido de boneco personalizado feito no site."
};

// Congela o objeto para evitar mutações acidentais durante a execução
Object.freeze(CONFIG);