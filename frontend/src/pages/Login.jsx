import { useState } from 'react'
import { useNavigate, Navigate, Link } from 'react-router-dom'
import CONFIG from '../config.js'

// ===== TELA DE LOGIN ADMIN =====
export default function Login() {
  const [email, setEmail]   = useState('')
  const [senha, setSenha]   = useState('')
  const [erro, setErro]     = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  if (localStorage.getItem('trokets_token')) {
    return <Navigate to="/admin" replace />
  }

  async function handleLogin(e) {
    e.preventDefault()
    setErro('')
    if (!email || !senha) { setErro('Preencha e-mail e senha.'); return }

    setLoading(true)
    try {
      const res  = await fetch(`${CONFIG.API_BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, senha }),
      })
      const data = await res.json()

      if (!res.ok)          { setErro(data.erro || 'Credenciais inválidas.'); return }
      if (data.role !== 'ADMIN') { setErro('Acesso restrito a administradores.'); return }

      localStorage.setItem('trokets_token', data.token)
      localStorage.setItem('trokets_email', data.email)
      navigate('/admin', { replace: true })
    } catch {
      setErro('Não foi possível conectar ao servidor.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{
      minHeight: '100vh',
      background: 'linear-gradient(135deg, var(--green-dark) 0%, var(--green) 100%)',
      display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 24,
    }}>
      <div style={{
        background: '#fff', borderRadius: 24, padding: '48px 44px',
        width: '100%', maxWidth: 420,
        boxShadow: '0 20px 60px rgba(0,0,0,.2)',
      }}>
        {/* Logo */}
        <div style={{ textAlign: 'center', marginBottom: 8 }}>
          <img src="/assets/img/logo.png" alt="Trokets" style={{ height: 56 }} />
        </div>
        <div style={{
          textAlign: 'center', fontSize: '.75rem', fontWeight: 700,
          letterSpacing: '.1em', textTransform: 'uppercase',
          color: 'var(--gray)', marginBottom: 32,
        }}>Painel Administrativo</div>

        <h1 style={{ fontFamily: 'Playfair Display,serif', fontSize: '1.6rem', color: 'var(--green-dark)', marginBottom: 6 }}>Entrar</h1>
        <p style={{ fontSize: '.88rem', color: 'var(--gray)', marginBottom: 28 }}>Acesso restrito para administradores da Trokets.</p>

        {/* Erro */}
        {erro && (
          <div style={{
            background: '#fde8e8', border: '1.5px solid #f5c6c6',
            color: 'var(--danger)', borderRadius: 10, padding: '11px 16px',
            fontSize: '.85rem', fontWeight: 700, marginBottom: 16,
          }}>{erro}</div>
        )}

        {/* Formulário */}
        <form onSubmit={handleLogin}>
          <Campo label="E-mail" type="email" value={email} onChange={e => setEmail(e.target.value)} placeholder="admin@trokets.com" />
          <Campo label="Senha"  type="password" value={senha} onChange={e => setSenha(e.target.value)} placeholder="••••••••" />

          <button type="submit" disabled={loading} style={{
            width: '100%', background: 'var(--green)', color: '#fff', border: 'none',
            padding: 14, borderRadius: 50, fontFamily: 'Nunito,sans-serif',
            fontWeight: 900, fontSize: '1rem', cursor: loading ? 'not-allowed' : 'pointer',
            marginTop: 8, opacity: loading ? .6 : 1, transition: 'background .2s',
          }}>
            {loading ? 'Entrando...' : 'Entrar no painel'}
          </button>
        </form>

        <Link to="/" style={{
          display: 'block', textAlign: 'center', marginTop: 20,
          fontSize: '.85rem', color: 'var(--gray)', textDecoration: 'none', fontWeight: 700,
        }}>← Voltar para a loja</Link>
      </div>
    </div>
  )
}

function Campo({ label, type, value, onChange, placeholder }) {
  return (
    <div style={{ marginBottom: 18 }}>
      <label style={{ display: 'block', fontSize: '.83rem', fontWeight: 700, color: 'var(--dark)', marginBottom: 7 }}>{label}</label>
      <input
        type={type} value={value} onChange={onChange} placeholder={placeholder}
        style={{
          width: '100%', padding: '12px 16px',
          border: '2px solid var(--gray-light)', borderRadius: 12,
          fontFamily: 'Nunito,sans-serif', fontSize: '.95rem', color: 'var(--dark)',
          outline: 'none', transition: 'border-color .2s, box-shadow .2s',
        }}
        onFocus={e => { e.target.style.borderColor = 'var(--green)'; e.target.style.boxShadow = '0 0 0 3px rgba(77,110,38,.12)' }}
        onBlur={e =>  { e.target.style.borderColor = 'var(--gray-light)'; e.target.style.boxShadow = '' }}
      />
    </div>
  )
}
