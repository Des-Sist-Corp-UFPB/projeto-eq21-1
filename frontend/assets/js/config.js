// CONFIGURAÇÕES GLOBAIS DA APLICAÇÃO

const CONFIG = {
  // URL base do servidor Back-end Spring Boot
  API_BASE: "http://localhost:8080",
  
  // Número do WhatsApp da Trokets formate com código do país e DDD (Ex: 5583999999999)
  WHATSAPP_NUMERO: "5583999999999",
  
  // Mensagem padrão de saudação ao abrir o link
  WHATSAPP_SAUDACAO: "Olá! Gostaria de confirmar o meu pedido de boneco personalizado feito no site."
};

// Congela o objeto para evitar mutações acidentais durante a execução
Object.freeze(CONFIG);