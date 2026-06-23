import { Link } from 'react-router-dom'

// ===== NAVBAR FIXA =====
export default function Navbar() {
  return (
    <nav style={{
      position: 'fixed', top: 0, left: 0, right: 0, zIndex: 100,
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      padding: '14px 5%',
      background: 'rgba(250,245,236,.93)',
      backdropFilter: 'blur(12px)',
      borderBottom: '2px solid var(--cream)',
    }}>
      {/* Logo */}
      <Link to="/">
        <img src="/assets/img/logo.png" alt="Trokets" style={{ height: 52 }} />
      </Link>

      {/* Links */}
      <ul className="nav-links" style={{
        display: 'flex', gap: 28, listStyle: 'none',
      }}>
        {[
          { label: 'Vitrine',   href: '/#vitrine'  },
          { label: 'Preços',    href: '/#precos'   },
          { label: 'Pagamento', href: '/#info'     },
          { label: 'Instagram', href: 'https://instagram.com/trokets', ext: true },
        ].map(({ label, href, ext }) => (
          <li key={label}>
            {ext
              ? <a href={href} target="_blank" rel="noreferrer" style={linkStyle}>{label}</a>
              : <a href={href} style={linkStyle}>{label}</a>
            }
          </li>
        ))}
        <li>
          <Link to="/pedido" style={{
            ...linkStyle,
            background: 'var(--green)', color: '#fff',
            padding: '9px 22px', borderRadius: 50,
          }}>
            Fazer Pedido
          </Link>
        </li>
      </ul>
    </nav>
  )
}

const linkStyle = {
  textDecoration: 'none',
  fontWeight: 700,
  fontSize: '.95rem',
  color: 'var(--green)',
  letterSpacing: '.03em',
  transition: 'color .2s',
}
