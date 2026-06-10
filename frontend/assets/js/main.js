// ===== VITRINE: busca do back-end Java =====

async function carregarVitrine() {
  const grid = document.getElementById('vitrine-grid');
  if (!grid) return; // Garante que só roda se o elemento existir na página

  try {
    // Repare que aqui usamos o CONFIG.API_BASE que criamos no passo anterior!
    const res = await fetch(`${CONFIG.API_BASE}/api/funkos`);
    if (!res.ok) throw new Error('Sem resposta do servidor');
    const data = await res.json();
    // A API retorna Page<FunkoResponse> — os itens ficam em data.content
    const bonecos = Array.isArray(data) ? data : (data.content ?? []);

    if (!bonecos.length) {
      grid.innerHTML = `
        <div class="empty-state">
          <div class="icon">🎭</div>
          <p>Nenhum boneco cadastrado ainda. Em breve novidades!</p>
        </div>`;
      return;
    }

    grid.innerHTML = bonecos.map(b => `
      <div class="card">
        <div class="card-img">
          ${b.imagemUrl
            ? `<img src="${b.imagemUrl}" alt="${b.nome}" style="width:100%;height:100%;object-fit:cover;" />`
            : '🪆'}
        </div>
        <div class="card-body">
          <span class="card-tag">${b.estilo || 'Personalizado'}</span>
          <div class="card-name">${b.nome}</div>
          <p class="card-desc">${b.descricao || ''}</p>
          <div class="card-price">${
            b.preco
              ? `A partir de R$ ${Number(b.preco).toFixed(2).replace('.',',')}`
              : 'Valor a combinar'
          }</div>
        </div>
      </div>`).join('');

  } catch (e) {
    // Fallback: exibe cards de exemplo quando o back não está disponível
    const exemplos = [
      { nome: 'Casal Funko Pop', estilo: 'Funko Pop', descricao: 'Dois personagens personalizados com roupas e acessórios únicos.', preco: 70, emoji: '👫' },
      { nome: 'Pet Chibi', estilo: 'Chibi', descricao: 'Seu bichinho de estimação em versão fofa e estilizada.', preco: 25, emoji: '🐾' },
      { nome: 'Herói Funko', estilo: 'Funko Pop', descricao: 'Personagem de quadrinhos ou série favorita em biscuit.', preco: 35, emoji: '🦸' },
      { nome: 'Personagem Chibi', estilo: 'Chibi', descricao: 'Estilo japonês chibi para qualquer personagem ou pessoa real.', preco: 25, emoji: '🌸' },
    ];
    grid.innerHTML = exemplos.map(b => `
      <div class="card">
        <div class="card-img" style="font-size:4rem;display:flex;align-items:center;justify-content:center;">${b.emoji}</div>
        <div class="card-body">
          <span class="card-tag">${b.estilo}</span>
          <div class="card-name">${b.nome}</div>
          <p class="card-desc">${b.descricao}</p>
          <div class="card-price">A partir de R$ ${b.preco.toFixed(2).replace('.',',')}</div>
        </div>
      </div>`).join('');
  }
}

// Inicializa a busca assim que o arquivo é carregado
carregarVitrine();