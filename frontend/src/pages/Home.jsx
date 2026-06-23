import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import Navbar from '../components/Navbar.jsx'
import Footer from '../components/Footer.jsx'
import CONFIG from '../config.js'

// ===== HOME (Vitrine + Preços + Info) =====
export default function Home() {
  const [bonecos, setBonecos] = useState([])
  const [carregando, setCarregando] = useState(true)

  // Busca bonecos do backend
  useEffect(() => {
    fetch(`${CONFIG.API_BASE}/api/funkos`)
      .then(r => r.json())
      .then(data => {
        const lista = Array.isArray(data) ? data : (data.content ?? [])
        setBonecos(lista)
      })
      .catch(() => setBonecos(EXEMPLOS))
      .finally(() => setCarregando(false))
  }, [])

  return (
    <>
      <Navbar />

      {/* ===== HERO ===== */}
      <section className="hero" style={{
        minHeight: '100vh',
        background: 'linear-gradient(135deg, var(--offwhite) 55%, var(--cream-light) 100%)',
        display: 'flex', alignItems: 'center',
        padding: '110px 5% 60px', gap: 40, flexWrap: 'wrap',
      }}>
        <div style={{ flex: '1 1 340px' }}>
          <span style={{
            display: 'inline-block', background: 'var(--cream)',
            color: 'var(--green-dark)', fontWeight: 700, fontSize: '.8rem',
            letterSpacing: '.12em', textTransform: 'uppercase',
            padding: '5px 16px', borderRadius: 50, marginBottom: 20,
          }}>✦ Feitos à mão com amor</span>

          <h1 style={{
            fontFamily: 'Playfair Display, serif',
            fontSize: 'clamp(2.4rem,5vw,4rem)', lineHeight: 1.1,
            color: 'var(--green-dark)',
          }}>
            Seu personagem<br />em <span style={{ color: 'var(--cream-dark)' }}>biscuit</span>,<br />do seu jeito
          </h1>

          <p style={{
            margin: '20px 0 34px', fontSize: '1.08rem',
            color: 'var(--gray)', maxWidth: 440, lineHeight: 1.7,
          }}>
            Bonecos únicos e personalizados no estilo Funko Pop ou Chibi.
            Presenteie quem você ama com algo que só existe no mundo inteiro.
          </p>

          <div className="hero-btns" style={{ display: 'flex', gap: 14, flexWrap: 'wrap' }}>
            <Link to="/pedido" className="btn-primary">🎨 Fazer Encomenda</Link>
            <a href="#vitrine" className="btn-outline">Ver Vitrine</a>
          </div>
        </div>

        <div className="hero-visual" style={{ flex: '1 1 300px', display: 'flex', justifyContent: 'center' }}>
          <img src="/assets/img/logo.png" alt="Trokets"
            style={{ width: 'min(340px,90vw)', filter: 'drop-shadow(0 12px 40px rgba(77,110,38,.22))', animation: 'float 4s ease-in-out infinite' }} />
        </div>
      </section>

      {/* ===== INFO STRIP ===== */}
      <div className="info-strip" style={{ background: 'var(--green)', display: 'flex', overflow: 'hidden' }}>
        {[
          { icon: '🧱', text: 'Biscuit Artesanal' },
          { icon: '⏱️', text: 'Entrega em 10–15 dias' },
          { icon: '💳', text: 'Pix · Cartão · Espécie' },
          { icon: '📦', text: 'Pedido 100% personalizado' },
        ].map(({ icon, text }) => (
          <div key={text} className="info-strip-item" style={{
            flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center',
            gap: 10, padding: '18px 16px',
            borderRight: '1px solid rgba(255,255,255,.2)',
            color: '#fff', fontWeight: 700, fontSize: '.9rem',
          }}>
            <span style={{ fontSize: '1.4rem' }}>{icon}</span> {text}
          </div>
        ))}
      </div>

      {/* ===== VITRINE ===== */}
      <section id="vitrine" style={{ padding: '80px 5%' }}>
        <div style={{ marginBottom: 48 }}>
          <p className="section-label">✦ Nossos trabalhos</p>
          <h2 className="section-title">Vitrine de Bonecos</h2>
          <p className="section-sub">Cada peça é única e feita à mão. Veja alguns dos nossos trabalhos e inspire-se!</p>
        </div>

        <div className="vitrine-grid" style={{
          display: 'grid', gridTemplateColumns: 'repeat(auto-fill,minmax(240px,1fr))', gap: 28,
        }}>
          {carregando
            ? [1,2,3,4].map(i => (
                <div key={i} className="skeleton" style={{ height: 340, borderRadius: 18 }} />
              ))
            : bonecos.map(b => <CardBoneco key={b.id} boneco={b} />)
          }
        </div>
      </section>

      {/* ===== PREÇOS ===== */}
      <section id="precos" style={{ padding: '80px 5%', background: 'var(--cream-light)' }}>
        <div style={{ marginBottom: 48 }}>
          <p className="section-label">✦ Investimento</p>
          <h2 className="section-title">Preços & Estilos</h2>
          <p className="section-sub">Escolha o estilo que mais combina com o seu personagem.</p>
        </div>
        <div className="precos-grid" style={{
          display: 'grid', gridTemplateColumns: 'repeat(auto-fit,minmax(240px,1fr))',
          gap: 24, maxWidth: 900,
        }}>
          <CardPreco icon="🌸" nome="Chibi" desc="Estilo japonês fofo e compacto. Cabeça grande, corpo pequeninho." valor="R$ 25" obs="A partir de" />
          <CardPreco icon="🎭" nome="Funko Pop" desc="Estilo icônico com cabeça grande e olhos pretos. Ideal para heróis, séries e casais." valor="R$ 35" obs="A partir de" destaque />
          <CardPreco icon="✨" nome="Outro estilo" desc="Tem uma ideia diferente? Realista, anime, OC... Vamos conversar!" valor="A combinar" obs="" />
        </div>
      </section>

      {/* ===== PAGAMENTO & PRAZO ===== */}
      <section id="info" style={{ padding: '80px 5%' }}>
        <div style={{ marginBottom: 48 }}>
          <p className="section-label">✦ Como funciona</p>
          <h2 className="section-title">Pagamento & Prazo</h2>
          <p className="section-sub">Tudo simples e combinado direto pelo WhatsApp.</p>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit,minmax(200px,1fr))', gap: 20, maxWidth: 900 }}>
          <CardInfo icon="⚡" title="Pix" desc="Pagamento instantâneo." />
          <CardInfo icon="💳" title="Cartão" desc="Débito ou crédito. Consulte condições." />
          <CardInfo icon="💵" title="Espécie" desc="Na entrega ou retirada." />
          <CardInfo icon="📦" title="Prazo" desc="dias úteis após confirmação." big="10–15" />
        </div>
      </section>

      {/* ===== CTA ===== */}
      <section style={{ background: 'var(--green)', padding: '70px 5%', textAlign: 'center' }}>
        <h2 style={{ fontFamily: 'Playfair Display,serif', fontSize: 'clamp(1.8rem,3.5vw,2.8rem)', color: '#fff', marginBottom: 12 }}>
          Pronto para ter seu boneco? 🌵
        </h2>
        <p style={{ color: 'rgba(255,255,255,.8)', fontSize: '1rem', marginBottom: 32, lineHeight: 1.7 }}>
          Faça sua encomenda e receba um boneco único, feito à mão só para você!
        </p>
        <Link to="/pedido" style={{
          background: 'var(--cream)', color: 'var(--green-dark)',
          border: 'none', padding: '16px 40px', borderRadius: 50,
          fontFamily: 'Nunito,sans-serif', fontWeight: 900, fontSize: '1.05rem',
          cursor: 'pointer', textDecoration: 'none', display: 'inline-block',
          transition: 'background .2s',
        }}>🎨 Quero meu boneco!</Link>
      </section>

      <Footer />
    </>
  )
}

// ===== SUB-COMPONENTES =====
function CardBoneco({ boneco: b }) {
  return (
    <div style={{
      background: '#fff', borderRadius: 'var(--radius)',
      overflow: 'hidden', boxShadow: 'var(--shadow)',
      transition: 'transform .22s, box-shadow .22s', cursor: 'pointer',
    }}
      onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-6px)'; e.currentTarget.style.boxShadow = '0 12px 40px rgba(77,110,38,.18)' }}
      onMouseLeave={e => { e.currentTarget.style.transform = ''; e.currentTarget.style.boxShadow = 'var(--shadow)' }}
    >
      <div style={{
        width: '100%', aspectRatio: '1/1', background: 'var(--cream-light)',
        display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '3.5rem',
      }}>
        {b.imagemUrl
          ? <img src={b.imagemUrl} alt={b.nome} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
          : '🪆'}
      </div>
      <div style={{ padding: '18px 20px 22px' }}>
        <span style={{
          display: 'inline-block', background: 'var(--cream)', color: 'var(--green-dark)',
          fontSize: '.72rem', fontWeight: 700, letterSpacing: '.08em',
          textTransform: 'uppercase', padding: '3px 11px', borderRadius: 50, marginBottom: 9,
        }}>{b.franquia || 'Personalizado'}</span>
        <div style={{ fontFamily: 'Playfair Display,serif', fontSize: '1.2rem', marginBottom: 6 }}>{b.nome}</div>
        <div style={{ fontWeight: 900, fontSize: '1.1rem', color: 'var(--green)' }}>
          {b.preco ? `A partir de R$ ${Number(b.preco).toFixed(2).replace('.',',')}` : 'Valor a combinar'}
        </div>
      </div>
    </div>
  )
}

function CardPreco({ icon, nome, desc, valor, obs, destaque }) {
  return (
    <div style={{
      background: '#fff', borderRadius: 'var(--radius)', padding: '32px 28px',
      boxShadow: 'var(--shadow)', position: 'relative',
      border: destaque ? '3px solid var(--green)' : 'none',
      transition: 'transform .2s',
    }}>
      {destaque && (
        <div style={{
          position: 'absolute', top: -14, left: '50%', transform: 'translateX(-50%)',
          background: 'var(--green)', color: '#fff', fontSize: '.72rem',
          fontWeight: 800, letterSpacing: '.1em', textTransform: 'uppercase',
          padding: '4px 16px', borderRadius: 50, whiteSpace: 'nowrap',
        }}>⭐ Mais pedido</div>
      )}
      <div style={{ fontSize: '2.8rem', marginBottom: 14 }}>{icon}</div>
      <div style={{ fontFamily: 'Playfair Display,serif', fontSize: '1.4rem', color: 'var(--green-dark)', marginBottom: 6 }}>{nome}</div>
      <p style={{ fontSize: '.88rem', color: 'var(--gray)', lineHeight: 1.6, marginBottom: 20 }}>{desc}</p>
      <div style={{ fontSize: '2rem', fontWeight: 900, color: 'var(--green)', marginBottom: 4 }}>
        {obs && <span style={{ fontSize: '1rem', fontWeight: 700 }}>{obs} </span>}{valor}
      </div>
      <div style={{ fontSize: '.78rem', color: 'var(--gray)' }}>Valor final a combinar conforme detalhes</div>
    </div>
  )
}

function CardInfo({ icon, title, desc, big }) {
  return (
    <div style={{
      background: '#fff', borderRadius: 'var(--radius)', padding: '28px 24px',
      boxShadow: 'var(--shadow)', display: 'flex', flexDirection: 'column', gap: 10,
    }}>
      <div style={{
        width: 48, height: 48, background: 'var(--cream-light)', borderRadius: 12,
        display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.6rem',
      }}>{icon}</div>
      <div style={{ fontWeight: 800, fontSize: '1rem' }}>{title}</div>
      {big && <div style={{ fontSize: '1.8rem', fontWeight: 900, color: 'var(--green)', lineHeight: 1 }}>{big}</div>}
      <div style={{ fontSize: '.88rem', color: 'var(--gray)', lineHeight: 1.6 }}>{desc}</div>
    </div>
  )
}

// Fallback quando o back não responde
const EXEMPLOS = [
  { id:1, nome:'Casal Funko Pop', franquia:'Funko Pop', preco:70 },
  { id:2, nome:'Pet Chibi',       franquia:'Chibi',     preco:25 },
  { id:3, nome:'Herói Funko',     franquia:'Funko Pop', preco:35 },
  { id:4, nome:'Personagem Chibi',franquia:'Chibi',     preco:25 },
]
