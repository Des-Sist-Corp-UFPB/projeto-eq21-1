import { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import Toast, { useToast } from '../components/Toast.jsx'
import CONFIG from '../config.js'

// ===== PAINEL ADMIN =====
export default function Admin() {
  const [bonecos, setBonecos]       = useState([])
  const [carregando, setCarregando] = useState(true)
  const [modal, setModal]           = useState(null)   // null | 'form' | 'del'
  const [editando, setEditando]     = useState(null)   // boneco em edição
  const [delId, setDelId]           = useState(null)
  const { toast, mostrar }          = useToast()
  const navigate = useNavigate()

  const email = localStorage.getItem('trokets_email') || 'admin'

  function headers() {
    return { Authorization: `Bearer ${localStorage.getItem('trokets_token')}` }
  }

  function logout() {
    localStorage.clear()
    navigate('/login', { replace: true })
  }

  async function carregar() {
    setCarregando(true)
    try {
      const res  = await fetch(`${CONFIG.API_BASE}/api/funkos`)
      const data = await res.json()
      setBonecos(Array.isArray(data) ? data : (data.content ?? []))
    } catch {
      mostrar('Erro ao conectar ao servidor.', true)
    } finally {
      setCarregando(false)
    }
  }

  useEffect(() => { carregar() }, [])

  async function salvar(form) {
    const { id, nome, franquia, preco, imagem } = form
    const url    = id ? `${CONFIG.API_BASE}/api/funkos/${id}` : `${CONFIG.API_BASE}/api/funkos`
    const method = id ? 'PUT' : 'POST'

    // Multipart Form Data — necessário para enviar a foto como arquivo
    const data = new FormData()
    data.append('nome', nome)
    data.append('franquia', franquia)
    data.append('preco', parseFloat(preco))
    if (imagem) data.append('imagem', imagem) // arquivo (File) — só envia se uma nova foto foi selecionada

    try {
      const res = await fetch(url, {
        method,
        headers: headers(), // Authorization: Bearer <token> — necessário no backend
        body: data, // sem 'Content-Type': o browser define o boundary do multipart automaticamente
      })
      if (res.status === 401 || res.status === 403) { logout(); return }
      if (!res.ok) throw new Error()
      setModal(null)
      mostrar(id ? '✅ Boneco atualizado!' : '✅ Boneco adicionado!')
      carregar()
    } catch {
      mostrar('Erro ao salvar boneco.', true)
    }
  }

  async function excluir() {
    try {
      const res = await fetch(`${CONFIG.API_BASE}/api/funkos/${delId}`, { method: 'DELETE', headers: headers() })
      if (res.status === 401 || res.status === 403) { logout(); return }
      setModal(null)
      mostrar('🗑️ Boneco excluído!')
      carregar()
    } catch {
      mostrar('Erro ao excluir.', true)
    }
  }

  return (
    <div className="admin-layout" style={{ display: 'flex', minHeight: '100vh', fontFamily: 'Nunito,sans-serif' }}>

      {/* ===== SIDEBAR ===== */}
      <aside className="sidebar" style={{
        width: 240, background: 'var(--green-dark)', color: '#fff',
        padding: '28px 0', display: 'flex', flexDirection: 'column',
        position: 'fixed', top: 0, left: 0, bottom: 0, zIndex: 50,
      }}>
        <div className="sidebar-logo" style={{ padding: '0 24px 28px', borderBottom: '1px solid rgba(255,255,255,.12)' }}>
          <img src="/assets/img/logo.png" alt="Trokets" style={{ height: 44, filter: 'brightness(0) invert(1)' }} />
        </div>
        <div style={{ fontSize: '.7rem', fontWeight: 700, letterSpacing: '.1em', textTransform: 'uppercase', color: 'rgba(255,255,255,.4)', padding: '20px 24px 8px' }}>Menu</div>
        <ul className="sidebar-menu" style={{ listStyle: 'none', padding: '0 12px', flex: 1 }}>
          {[
            { emoji: '🪆', label: 'Bonecos', to: null },
            { emoji: '🏪', label: 'Ver loja', to: '/', ext: false },
            { emoji: '💬', label: 'WhatsApp', to: `https://wa.me/${CONFIG.WHATSAPP_NUMERO}`, ext: true },
            { emoji: '📸', label: 'Instagram', to: 'https://instagram.com/trokets_biscuit', ext: true },
          ].map(({ emoji, label, to, ext }) => (
            <li key={label} style={{ marginBottom: 2 }}>
              {ext
                ? <a href={to} target="_blank" rel="noreferrer" style={sideLink}>{emoji} {label}</a>
                : to
                  ? <Link to={to} style={sideLink}>{emoji} {label}</Link>
                  : <span style={{ ...sideLink, background: 'var(--green)', color: '#fff', display: 'flex' }}>{emoji} {label}</span>
              }
            </li>
          ))}
        </ul>
        <div className="sidebar-footer" style={{ padding: '16px 12px', borderTop: '1px solid rgba(255,255,255,.12)' }}>
          <div style={{ padding: '10px 14px', display: 'flex', alignItems: 'center', gap: 10, marginBottom: 6 }}>
            <div style={{
              width: 32, height: 32, borderRadius: '50%', background: 'var(--cream)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontSize: '.8rem', fontWeight: 900, color: 'var(--green-dark)',
            }}>{email[0].toUpperCase()}</div>
            <div style={{ fontSize: '.78rem', color: 'rgba(255,255,255,.6)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{email}</div>
          </div>
          <button onClick={logout} style={{
            width: '100%', background: 'rgba(255,255,255,.08)',
            border: '1px solid rgba(255,255,255,.15)',
            color: 'rgba(255,255,255,.7)', padding: 9, borderRadius: 10,
            fontFamily: 'Nunito,sans-serif', fontWeight: 700, fontSize: '.85rem',
            cursor: 'pointer',
          }}>Sair</button>
        </div>
      </aside>

      {/* ===== MAIN ===== */}
      <main className="main-content" style={{ marginLeft: 240, flex: 1, padding: '36px 40px' }}>
        <div className="header-panel" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 32, flexWrap: 'wrap', gap: 16 }}>
          <div>
            <h1 style={{ fontFamily: 'Playfair Display,serif', fontSize: '1.8rem', color: 'var(--green-dark)' }}>Bonecos</h1>
            <p style={{ fontSize: '.9rem', color: 'var(--gray)', marginTop: 3 }}>Gerencie os bonecos da vitrine.</p>
          </div>
          <button onClick={() => { setEditando(null); setModal('form') }} style={{
            background: 'var(--green)', color: '#fff', border: 'none',
            padding: '10px 22px', borderRadius: 50,
            fontFamily: 'Nunito,sans-serif', fontWeight: 800, fontSize: '.9rem',
            cursor: 'pointer', transition: 'background .2s',
          }}>+ Novo boneco</button>
        </div>

        {/* Tabela */}
        <div className="table-container" style={{ background: '#fff', borderRadius: 18, boxShadow: '0 2px 12px rgba(77,110,38,.08)', overflow: 'hidden' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ background: 'var(--offwhite)' }}>
                {['Imagem','Nome','Franquia','Preço','Ações'].map(h => (
                  <th key={h} style={{
                    padding: '14px 18px', textAlign: 'left',
                    fontSize: '.78rem', fontWeight: 800, color: 'var(--gray)',
                    textTransform: 'uppercase', letterSpacing: '.06em',
                    borderBottom: '1px solid var(--gray-light)',
                  }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {carregando
                ? <tr><td colSpan={5} style={{ textAlign: 'center', padding: 48, color: 'var(--gray)' }}>⏳ Carregando...</td></tr>
                : !bonecos.length
                  ? <tr><td colSpan={5} style={{ textAlign: 'center', padding: 48, color: 'var(--gray)' }}>🪆 Nenhum boneco cadastrado.</td></tr>
                  : bonecos.map(b => (
                    <tr key={b.id} style={{ borderBottom: '1px solid var(--gray-light)' }}>
                      <td style={{ padding: '14px 18px' }}>
                        <div style={{
                          width: 52, height: 52, borderRadius: 10, overflow: 'hidden',
                          background: 'var(--cream-light)',
                          display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.6rem',
                        }}>
                          {b.imagemUrl ? <img src={b.imagemUrl} alt={b.nome} style={{ width: '100%', height: '100%', objectFit: 'cover' }} /> : '🪆'}
                        </div>
                      </td>
                      <td style={{ padding: '14px 18px', fontWeight: 700 }}>{b.nome}</td>
                      <td style={{ padding: '14px 18px' }}>
                        <span style={{
                          display: 'inline-block', background: 'var(--cream)', color: 'var(--green-dark)',
                          fontSize: '.72rem', fontWeight: 700, padding: '3px 10px', borderRadius: 50,
                          textTransform: 'uppercase', letterSpacing: '.05em',
                        }}>{b.franquia}</span>
                      </td>
                      <td style={{ padding: '14px 18px', fontWeight: 900, color: 'var(--green)' }}>
                        R$ {Number(b.preco).toFixed(2).replace('.',',')}
                      </td>
                      <td style={{ padding: '14px 18px' }}>
                        <div style={{ display: 'flex', gap: 8 }}>
                          <BtnAcao onClick={() => { setEditando(b); setModal('form') }} cor="var(--cream-light)" label="✏️ Editar" />
                          <BtnAcao onClick={() => { setDelId(b.id); setModal('del') }} cor="var(--danger-light)" label="🗑️ Excluir" danger />
                        </div>
                      </td>
                    </tr>
                  ))
              }
            </tbody>
          </table>
        </div>
      </main>

      {/* ===== MODAL FORM ===== */}
      {modal === 'form' && <ModalForm boneco={editando} onSalvar={salvar} onFechar={() => setModal(null)} />}

      {/* ===== MODAL DELETE ===== */}
      {modal === 'del' && (
        <div style={overlayStyle} onClick={() => setModal(null)}>
          <div style={{ ...modalCard, maxWidth: 400, textAlign: 'center' }} onClick={e => e.stopPropagation()}>
            <div style={{ fontFamily: 'Playfair Display,serif', fontSize: '1.4rem', color: 'var(--danger)', marginBottom: 12 }}>Excluir boneco?</div>
            <p style={{ color: 'var(--gray)', marginBottom: 24 }}>Esta ação não pode ser desfeita.</p>
            <div style={{ display: 'flex', gap: 10, justifyContent: 'center' }}>
              <button onClick={() => setModal(null)} style={btnCancelar}>Cancelar</button>
              <button onClick={excluir} style={{ ...btnConfirmar, background: 'var(--danger)' }}>Sim, excluir</button>
            </div>
          </div>
        </div>
      )}

      <Toast toast={toast} />
    </div>
  )
}

// ===== MODAL FORMULÁRIO =====
function ModalForm({ boneco, onSalvar, onFechar }) {
  const [form, setForm] = useState({
    id: boneco?.id || '',
    nome: boneco?.nome || '',
    franquia: boneco?.franquia || 'Funko Pop',
    preco: boneco?.preco || '',
    imagem: null, // File novo selecionado (se houver)
  })
  // Preview: mostra a imagem já cadastrada até o usuário escolher uma nova
  const [preview, setPreview] = useState(boneco?.imagemUrl || '')

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }))

  function selecionarFoto(file) {
    if (!file) return
    set('imagem', file)
    const reader = new FileReader()
    reader.onload = e => setPreview(e.target.result)
    reader.readAsDataURL(file)
  }

  return (
    <div style={overlayStyle} onClick={onFechar}>
      <div style={modalCard} onClick={e => e.stopPropagation()}>
        <div style={{ fontFamily: 'Playfair Display,serif', fontSize: '1.4rem', color: 'var(--green-dark)', marginBottom: 4 }}>
          {boneco ? 'Editar boneco' : 'Novo boneco'}
        </div>
        <div style={{ fontSize: '.85rem', color: 'var(--gray)', marginBottom: 24 }}>
          {boneco ? `Editando: ${boneco.nome}` : 'Preencha os dados para adicionar à vitrine.'}
        </div>

        <Campo label="Nome *"      value={form.nome}      onChange={v => set('nome', v)}      placeholder="Ex: Naruto Uzumaki" />
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14, marginBottom: 18 }}>
          <div>
            <label style={labelStyle}>Franquia *</label>
            <select value={form.franquia} onChange={e => set('franquia', e.target.value)} style={inputStyle}>
              <option>Funko Pop</option>
              <option>Chibi</option>
              <option>Outro</option>
            </select>
          </div>
          <div>
            <label style={labelStyle}>Preço (R$) *</label>
            <input type="number" value={form.preco} onChange={e => set('preco', e.target.value)} placeholder="35.00" style={inputStyle} />
          </div>
        </div>

        {/* Upload de foto — Multipart Form Data */}
        <div style={{ marginBottom: 18 }}>
          <label style={labelStyle}>Foto do boneco</label>
          <label style={{
            display: 'flex', alignItems: 'center', gap: 14,
            border: '2px dashed var(--cream-dark)', borderRadius: 12,
            padding: '12px 14px', cursor: 'pointer', background: 'var(--offwhite)',
          }}>
            <input
              type="file" accept="image/*" style={{ display: 'none' }}
              onChange={e => selecionarFoto(e.target.files[0])}
            />
            <div style={{
              width: 52, height: 52, borderRadius: 10, overflow: 'hidden', flexShrink: 0,
              background: 'var(--cream-light)', display: 'flex', alignItems: 'center',
              justifyContent: 'center', fontSize: '1.5rem',
            }}>
              {preview ? <img src={preview} alt="Preview" style={{ width: '100%', height: '100%', objectFit: 'cover' }} /> : '🪆'}
            </div>
            <div style={{ fontSize: '.85rem', fontWeight: 700, color: 'var(--green-dark)' }}>
              {form.imagem ? form.imagem.name : preview ? 'Trocar foto' : 'Clique para selecionar uma foto'}
            </div>
          </label>
        </div>

        <div style={{ display: 'flex', gap: 10, marginTop: 24 }}>
          <button onClick={onFechar} style={btnCancelar}>Cancelar</button>
          <button onClick={() => onSalvar(form)} style={{ ...btnConfirmar, flex: 1 }}>
            {boneco ? 'Atualizar boneco' : 'Salvar boneco'}
          </button>
        </div>
      </div>
    </div>
  )
}

function Campo({ label, value, onChange, placeholder, type = 'text' }) {
  return (
    <div style={{ marginBottom: 18 }}>
      <label style={labelStyle}>{label}</label>
      <input type={type} value={value} onChange={e => onChange(e.target.value)} placeholder={placeholder} style={inputStyle} />
    </div>
  )
}

function BtnAcao({ onClick, label, danger }) {
  return (
    <button onClick={onClick} style={{
      border: 'none', borderRadius: 8, padding: '7px 14px',
      fontFamily: 'Nunito,sans-serif', fontWeight: 700, fontSize: '.82rem',
      cursor: 'pointer', transition: 'all .15s',
      background: danger ? 'var(--danger-light)' : 'var(--cream-light)',
      color: danger ? 'var(--danger)' : 'var(--green-dark)',
    }}>{label}</button>
  )
}

// ===== ESTILOS COMPARTILHADOS =====
const overlayStyle = {
  position: 'fixed', inset: 0, background: 'rgba(0,0,0,.45)',
  zIndex: 200, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 24,
}
const modalCard = {
  background: '#fff', borderRadius: 22, padding: '36px 36px 28px',
  width: '100%', maxWidth: 500,
}
const labelStyle = { display: 'block', fontSize: '.83rem', fontWeight: 700, color: 'var(--dark)', marginBottom: 7 }
const inputStyle = {
  width: '100%', padding: '11px 15px',
  border: '2px solid var(--gray-light)', borderRadius: 12,
  fontFamily: 'Nunito,sans-serif', fontSize: '.93rem', outline: 'none',
}
const btnCancelar = {
  background: 'transparent', color: 'var(--gray)',
  border: '2px solid var(--gray-light)', padding: '13px 22px',
  borderRadius: 50, fontFamily: 'Nunito,sans-serif', fontWeight: 800,
  fontSize: '.9rem', cursor: 'pointer',
}
const btnConfirmar = {
  background: 'var(--green)', color: '#fff', border: 'none',
  padding: '13px 24px', borderRadius: 50,
  fontFamily: 'Nunito,sans-serif', fontWeight: 900, fontSize: '.95rem', cursor: 'pointer',
}
const sideLink = {
  display: 'flex', alignItems: 'center', gap: 10,
  padding: '11px 14px', borderRadius: 10,
  color: 'rgba(255,255,255,.7)', textDecoration: 'none',
  fontWeight: 700, fontSize: '.9rem', transition: 'all .2s',
}
