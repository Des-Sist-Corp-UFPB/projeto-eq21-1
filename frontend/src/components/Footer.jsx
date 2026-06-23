import { Link } from 'react-router-dom'

// ===== FOOTER =====
export default function Footer() {
  return (
    <footer style={{ background: 'var(--green-dark)', color: 'rgba(255,255,255,.75)', padding: '56px 5% 32px' }}>
      <div className="footer-grid" style={{
        display: 'grid', gridTemplateColumns: '2fr 1fr 1fr',
        gap: 48, marginBottom: 48,
      }}>
        {/* Marca */}
        <div>
          <img src="/assets/img/logo.png" alt="Trokets"
            style={{ height: 48, marginBottom: 16, filter: 'brightness(0) invert(1)' }} />
          <p style={{ fontSize: '.88rem', lineHeight: 1.7, maxWidth: 280 }}>
            Bonecos únicos e personalizados no estilo Funko Pop ou Chibi, feitos à mão com amor.
          </p>
          <div style={{ display: 'flex', gap: 12, marginTop: 20 }}>
            {[
              { emoji: '📸', href: 'https://instagram.com/trokets', title: 'Instagram' },
              { emoji: '💬', href: 'https://wa.me/5584996980815', title: 'WhatsApp' },
            ].map(({ emoji, href, title }) => (
              <a key={title} href={href} target="_blank" rel="noreferrer" title={title} style={{
                width: 40, height: 40, borderRadius: '50%',
                background: 'rgba(255,255,255,.12)',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                textDecoration: 'none', fontSize: '1.1rem', color: '#fff',
                transition: 'background .2s',
              }}>{emoji}</a>
            ))}
          </div>
        </div>

        {/* Navegação */}
        <div>
          <div style={colTitle}>Navegação</div>
          <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: 10 }}>
            {[
              { label: 'Vitrine',      to: '/#vitrine' },
              { label: 'Preços',       to: '/#precos'  },
              { label: 'Fazer pedido', to: '/pedido'   },
            ].map(({ label, to }) => (
              <li key={label}>
                <Link to={to} style={footerLink}>{label}</Link>
              </li>
            ))}
          </ul>
        </div>

        {/* Contato */}
        <div>
          <div style={colTitle}>Contato</div>
          <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: 10 }}>
            <li><a href="https://wa.me/5584996980815" target="_blank" rel="noreferrer" style={footerLink}>WhatsApp</a></li>
            <li><a href="https://instagram.com/trokets" target="_blank" rel="noreferrer" style={footerLink}>@trokets</a></li>
          </ul>
          <div style={{ ...colTitle, marginTop: 24 }}>Horário</div>
          <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: 6, fontSize: '.88rem' }}>
            <li style={{ color: 'rgba(255,255,255,.7)' }}>Seg–Sex: 9h–18h</li>
            <li style={{ color: 'rgba(255,255,255,.7)' }}>Sáb: 9h–13h</li>
          </ul>
        </div>
      </div>

      {/* Bottom */}
      <div style={{
        borderTop: '1px solid rgba(255,255,255,.12)', paddingTop: 24,
        display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap',
        gap: 12, fontSize: '.82rem',
      }}>
        <span>© 2025 Trokets. Todos os direitos reservados.</span>
        <a href="https://instagram.com/trokets" target="_blank" rel="noreferrer"
          style={{ color: 'var(--cream)', textDecoration: 'none' }}>
          Feito com 🌵 e biscuit
        </a>
      </div>
    </footer>
  )
}

const colTitle = {
  fontSize: '.82rem', fontWeight: 800, letterSpacing: '.1em',
  textTransform: 'uppercase', color: 'var(--cream)', marginBottom: 16,
}
const footerLink = {
  color: 'rgba(255,255,255,.7)', textDecoration: 'none',
  fontSize: '.9rem', fontWeight: 600,
}
