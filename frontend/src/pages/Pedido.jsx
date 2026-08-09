import { useState, Fragment } from 'react'
import { Link } from 'react-router-dom'
import CONFIG from '../config.js'

// ===== FORMULÁRIO DE PEDIDO (3 etapas) =====
export default function Pedido() {
  const [passo, setPasso]     = useState(1)
  const [imagens, setImagens] = useState([])
  const [form, setForm]       = useState({
    nome: '', descricao: '', posicao: '', posicaoOutra: '',
    estilo: '', cliente: '', telefone: '', obs: '',
  })
  const [erros, setErros]  = useState({})
  const [toast, setToast]  = useState('')
  const [arrastando, setArrastando] = useState(false)

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }))

  function mostrarToast(msg) {
    setToast(msg)
    setTimeout(() => setToast(''), 3000)
  }

  // ===== VALIDAÇÃO PASSO 1 =====
  function validarP1() {
    const e = {}
    if (!form.nome.trim())     e.nome      = 'Informe o nome do personagem.'
    if (!form.descricao.trim())e.descricao = 'Descreva o personagem.'
    if (!form.posicao)         e.posicao   = 'Selecione uma posição.'
    if (!form.estilo)          e.estilo    = 'Selecione um estilo.'
    if (!form.cliente.trim())  e.cliente   = 'Informe seu nome.'
    if (!form.telefone.trim()) e.telefone  = 'Informe seu WhatsApp.'
    setErros(e)
    return Object.keys(e).length === 0
  }

  // ===== UPLOAD DE IMAGENS =====
  function processarImagens(files) {
    const arr = Array.from(files).filter(f => f.type.startsWith('image/'))
    if (imagens.length + arr.length > 5) { mostrarToast('Máximo de 5 imagens!'); return }
    arr.forEach(file => {
      const reader = new FileReader()
      reader.onload = e => setImagens(prev => [...prev, { nome: file.name, url: e.target.result }])
      reader.readAsDataURL(file)
    })
  }

  // ===== ENVIO WHATSAPP =====
  function enviarWhatsApp() {
    const posicaoFinal = form.posicao === 'Outra pose' && form.posicaoOutra
      ? `Outra: ${form.posicaoOutra}` : form.posicao
    const precos = { 'Funko Pop': 'A partir de R$ 35,00', 'Chibi': 'A partir de R$ 25,00', 'Outro': 'Valor a combinar' }

    let msg = `🌵 *NOVO PEDIDO – TROKETS*\n\n`
    msg += `👤 *Personagem:* ${form.nome}\n`
    msg += `📝 *Descrição:* ${form.descricao}\n`
    msg += `🧍 *Posição:* ${posicaoFinal}\n`
    msg += `🎭 *Estilo:* ${form.estilo}\n`
    msg += `💰 *Preço estimado:* ${precos[form.estilo] || 'A combinar'}\n`
    msg += `📸 *Fotos:* ${imagens.length > 0 ? `${imagens.length} foto(s) — envio em seguida` : 'Nenhuma'}\n`
    msg += `\n👤 *Cliente:* ${form.cliente}\n`
    msg += `📱 *WhatsApp:* ${form.telefone}\n`
    if (form.obs) msg += `\n💬 *Obs:* ${form.obs}\n`
    msg += `\n⏱️ *Prazo:* 10 a 15 dias úteis\n`
    msg += `\n_Pedido enviado pelo site Trokets_`

    window.open(`https://wa.me/${CONFIG.WHATSAPP_NUMERO}?text=${encodeURIComponent(msg)}`, '_blank')
    mostrarToast('✅ Pedido enviado! Finalize no WhatsApp.')
  }

  const precos = { 'Funko Pop': 'A partir de R$ 35,00', 'Chibi': 'A partir de R$ 25,00', 'Outro': 'Valor a combinar' }

  return (
    <div style={{ fontFamily: 'Nunito,sans-serif', background: 'var(--offwhite)', minHeight: '100vh' }}>

      {/* NAVBAR */}
      <nav style={{
        position: 'fixed', top: 0, left: 0, right: 0, zIndex: 100,
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '14px 5%', background: 'rgba(250,245,236,.93)',
        backdropFilter: 'blur(12px)', borderBottom: '2px solid var(--cream)',
      }}>
        <Link to="/"><img src="/assets/img/logo.png" alt="Trokets" style={{ height: 48 }} /></Link>
        <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: 8, textDecoration: 'none', fontWeight: 700, color: 'var(--green)', fontSize: '.9rem' }}>
          ← Voltar à loja
        </Link>
      </nav>

      {/* HEADER */}
      <div style={{
        padding: '120px 5% 48px', textAlign: 'center',
        background: 'linear-gradient(160deg, var(--offwhite) 60%, var(--cream-light) 100%)',
      }}>
        <span style={{ display: 'inline-block', background: 'var(--cream)', color: 'var(--green-dark)', fontWeight: 700, fontSize: '.78rem', letterSpacing: '.12em', textTransform: 'uppercase', padding: '5px 16px', borderRadius: 50, marginBottom: 16 }}>✦ Encomenda personalizada</span>
        <h1 style={{ fontFamily: 'Playfair Display,serif', fontSize: 'clamp(2rem,4vw,3rem)', color: 'var(--green-dark)', marginBottom: 12 }} className="page-title">Fazer meu pedido</h1>
        <p style={{ color: 'var(--gray)', fontSize: '1rem', maxWidth: 500, margin: '0 auto', lineHeight: 1.7 }}>
          Preencha os detalhes e enviaremos tudo pelo WhatsApp para confirmar.
        </p>
      </div>

      <div style={{ maxWidth: 780, margin: '0 auto', padding: '0 5% 80px' }}>

        {/* STEPS */}
        <div style={{ display: 'flex', alignItems: 'center', margin: '40px 0 36px' }}>
          {[1,2,3].map((n, i) => (
            <Fragment key={n}>
              <Step n={n} passo={passo} label={['Personagem','Referências','Confirmar'][i]} />
              {n < 3 && <div style={{ flex: 1, height: 2, background: passo > n ? 'var(--cream-dark)' : 'var(--gray-light)', marginBottom: 22 }} />}
            </Fragment>
          ))}
        </div>

        {/* ===== PASSO 1 ===== */}
        {passo === 1 && (
          <>
            <Card title="Sobre o personagem" sub="Nos conte quem vai virar boneco! 🎭">
              <Campo label="Nome do personagem *" erro={erros.nome}>
                <input value={form.nome} onChange={e => set('nome', e.target.value)} placeholder="Ex: Naruto, Meu cachorro Thor..." style={inputSt(erros.nome)} maxLength={100} />
              </Campo>
              <Campo label="Descrição *" erro={erros.descricao}>
                <textarea value={form.descricao} onChange={e => set('descricao', e.target.value)} placeholder="Características físicas, roupas, detalhes..." style={{ ...inputSt(erros.descricao), resize: 'vertical', minHeight: 90, lineHeight: 1.6 }} maxLength={600} />
                <div style={{ fontSize: '.78rem', color: 'var(--gray)', marginTop: 5 }}>Quanto mais detalhes, mais fiel ficará o boneco! 🎨</div>
              </Campo>

              <Campo label="Posição / Pose *" erro={erros.posicao}>
                <div className="posicao-grid" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
                  {['Em pé','Sentado','Pose de ação','Outra pose'].map(p => (
                    <button key={p} onClick={() => set('posicao', p)} style={{
                      border: `2px solid ${form.posicao === p ? 'var(--green)' : 'var(--gray-light)'}`,
                      borderRadius: 10, padding: '10px 14px', cursor: 'pointer',
                      background: form.posicao === p ? '#f3f8ec' : '#fff',
                      fontFamily: 'Nunito,sans-serif', fontWeight: 700, fontSize: '.85rem',
                      color: form.posicao === p ? 'var(--green-dark)' : 'var(--gray)',
                      textAlign: 'left', transition: 'all .2s',
                    }}>{p}</button>
                  ))}
                </div>
                {form.posicao === 'Outra pose' && (
                  <input value={form.posicaoOutra} onChange={e => set('posicaoOutra', e.target.value)} placeholder="Descreva a pose desejada..." style={{ ...inputSt(), marginTop: 10 }} maxLength={150} />
                )}
              </Campo>
            </Card>

            <Card title="Estilo de modelagem" sub="Escolha o estilo que mais combina.">
              <div className="estilo-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(3,1fr)', gap: 12 }}>
                {[
                  { value:'Funko Pop', icon:'🎭', price:'A partir de R$ 35' },
                  { value:'Chibi',     icon:'🌸', price:'A partir de R$ 25' },
                  { value:'Outro',     icon:'✨', price:'Valor a combinar'  },
                ].map(({ value, icon, price }) => (
                  <button key={value} onClick={() => set('estilo', value)} style={{
                    border: `2px solid ${form.estilo === value ? 'var(--green)' : 'var(--gray-light)'}`,
                    borderRadius: 14, padding: '16px 12px', textAlign: 'center',
                    cursor: 'pointer', background: form.estilo === value ? '#f3f8ec' : '#fff',
                    transition: 'all .2s',
                  }}>
                    <div style={{ fontSize: '2rem', marginBottom: 8 }}>{icon}</div>
                    <div style={{ fontWeight: 800, fontSize: '.9rem', color: form.estilo === value ? 'var(--green-dark)' : 'var(--dark)', marginBottom: 3 }}>{value}</div>
                    <div style={{ fontSize: '.78rem', color: 'var(--green)', fontWeight: 700 }}>{price}</div>
                  </button>
                ))}
              </div>
              {erros.estilo && <div style={{ fontSize: '.78rem', color: 'var(--danger)', marginTop: 10 }}>{erros.estilo}</div>}
            </Card>

            <Card title="Seus dados" sub="Para confirmarmos o pedido por WhatsApp.">
              <div className="field-row" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                <Campo label="Seu nome *" erro={erros.cliente}>
                  <input value={form.cliente} onChange={e => set('cliente', e.target.value)} placeholder="Como você se chama?" style={inputSt(erros.cliente)} maxLength={80} />
                </Campo>
                <Campo label="Seu WhatsApp *" erro={erros.telefone}>
                  <input value={form.telefone} onChange={e => set('telefone', e.target.value)} placeholder="(84) 99999-9999" style={inputSt(erros.telefone)} maxLength={20} />
                </Campo>
              </div>
              <Campo label="Observações extras">
                <textarea value={form.obs} onChange={e => set('obs', e.target.value)} placeholder="Prazo especial, presente, ocasião..." style={{ ...inputSt(), resize: 'vertical', minHeight: 70, lineHeight: 1.6 }} maxLength={400} />
              </Campo>
            </Card>

            <button onClick={() => { if (validarP1()) setPasso(2) }} style={btnPrimario}>
              Próximo: Fotos de referência →
            </button>
          </>
        )}

        {/* ===== PASSO 2 ===== */}
        {passo === 2 && (
          <>
            <Card title="Fotos de referência" sub="Quanto mais referências, mais fiel ao original ficará o boneco! (opcional)">
              <label
                onDragOver={e => { e.preventDefault(); setArrastando(true) }}
                onDragLeave={() => setArrastando(false)}
                onDrop={e => {
                  e.preventDefault()
                  setArrastando(false)
                  processarImagens(e.dataTransfer.files)
                }}
                style={{
                  display: 'block', border: `2px dashed ${arrastando ? 'var(--green)' : 'var(--cream-dark)'}`,
                  borderRadius: 14, padding: '28px 20px', textAlign: 'center',
                  cursor: 'pointer', background: arrastando ? '#f3f8ec' : 'var(--offwhite)', transition: 'all .2s',
                }}
              >
                <input type="file" accept="image/*" multiple style={{ display: 'none' }} onChange={e => processarImagens(e.target.files)} />
                <div style={{ fontSize: '2.2rem', marginBottom: 10 }}>📸</div>
                <div style={{ fontWeight: 800, fontSize: '.95rem', color: 'var(--green-dark)', marginBottom: 4 }}>Clique ou arraste fotos aqui</div>
                <div style={{ fontSize: '.82rem', color: 'var(--gray)' }}>Até 5 imagens · JPG, PNG, WEBP</div>
              </label>
              {imagens.length > 0 && (
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill,minmax(90px,1fr))', gap: 10, marginTop: 14 }}>
                  {imagens.map((img, i) => (
                    <div key={i} style={{ position: 'relative', aspectRatio: '1', borderRadius: 10, overflow: 'hidden', border: '2px solid var(--gray-light)' }}>
                      <img src={img.url} alt={img.nome} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                      <button onClick={() => setImagens(prev => prev.filter((_, j) => j !== i))} style={{
                        position: 'absolute', top: 4, right: 4, width: 22, height: 22,
                        background: 'rgba(0,0,0,.55)', borderRadius: '50%', border: 'none',
                        color: '#fff', fontSize: '.85rem', cursor: 'pointer', display: 'flex',
                        alignItems: 'center', justifyContent: 'center',
                      }}>✕</button>
                    </div>
                  ))}
                </div>
              )}
            </Card>
            <button onClick={() => setPasso(3)} style={btnPrimario}>Próximo: Revisar pedido →</button>
            <button onClick={() => setPasso(1)} style={btnSecundario}>← Voltar</button>
          </>
        )}

        {/* ===== PASSO 3 ===== */}
        {passo === 3 && (
          <>
            <Card title="Revise seu pedido" sub="Confirme os detalhes antes de enviar pelo WhatsApp.">
              <div style={{ background: 'var(--offwhite)', border: '2px solid var(--cream)', borderRadius: 14, padding: '20px 22px' }}>
                <div style={{ fontWeight: 800, fontSize: '.9rem', color: 'var(--green-dark)', marginBottom: 12, textTransform: 'uppercase', letterSpacing: '.06em' }}>📋 Resumo do pedido</div>
                {[
                  ['Personagem', form.nome],
                  ['Descrição',  form.descricao],
                  ['Posição',    form.posicao === 'Outra pose' ? `Outra: ${form.posicaoOutra}` : form.posicao],
                  ['Estilo',     form.estilo],
                  ['Preço estimado', precos[form.estilo] || 'A combinar'],
                  ['Fotos', imagens.length > 0 ? `${imagens.length} foto(s)` : 'Nenhuma'],
                  ['Seu nome',   form.cliente],
                  ['WhatsApp',   form.telefone],
                  ...(form.obs ? [['Observações', form.obs]] : []),
                ].map(([label, value]) => (
                  <div key={label} style={{ display: 'flex', justifyContent: 'space-between', fontSize: '.88rem', marginBottom: 6, gap: 12 }}>
                    <span style={{ color: 'var(--gray)', fontWeight: 600, whiteSpace: 'nowrap' }}>{label}</span>
                    <span style={{ color: 'var(--dark)', fontWeight: 700, textAlign: 'right' }}>{value || '—'}</span>
                  </div>
                ))}
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: 10, background: '#e7f7ee', border: '1.5px solid #a8d5b5', borderRadius: 12, padding: '12px 16px', marginTop: 16, fontSize: '.85rem', color: '#1a5c33', fontWeight: 700 }}>
                💬 Ao clicar em enviar, você será redirecionado para o WhatsApp da Trokets.
              </div>
            </Card>

            <Card>
              <div className="field-row" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                <div>
                  <div style={{ fontSize: '.78rem', fontWeight: 700, color: 'var(--gray)', textTransform: 'uppercase', letterSpacing: '.06em', marginBottom: 6 }}>Pagamento</div>
                  {['💳 Cartão','⚡ Pix','💵 Espécie'].map(p => <div key={p} style={{ fontSize: '.92rem', fontWeight: 700, color: 'var(--green-dark)', marginBottom: 4 }}>{p}</div>)}
                </div>
                <div>
                  <div style={{ fontSize: '.78rem', fontWeight: 700, color: 'var(--gray)', textTransform: 'uppercase', letterSpacing: '.06em', marginBottom: 6 }}>Prazo</div>
                  <div style={{ fontSize: '1.6rem', fontWeight: 900, color: 'var(--green)', lineHeight: 1 }}>10–15</div>
                  <div style={{ fontSize: '.85rem', color: 'var(--gray)', fontWeight: 700 }}>dias úteis após confirmação</div>
                </div>
              </div>
            </Card>

            <button onClick={enviarWhatsApp} style={{ ...btnPrimario, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 10, width: '100%' }}>
              💬 Enviar pedido pelo WhatsApp
            </button>
            <button onClick={() => setPasso(2)} style={btnSecundario}>← Voltar</button>
          </>
        )}
      </div>

      {/* TOAST */}
      {toast && (
        <div style={{
          position: 'fixed', bottom: 28, left: '50%', transform: 'translateX(-50%)',
          background: 'var(--green-dark)', color: '#fff', padding: '14px 28px',
          borderRadius: 50, fontWeight: 700, fontSize: '.92rem', zIndex: 999,
        }}>{toast}</div>
      )}
    </div>
  )
}

// ===== SUB-COMPONENTES =====
function Step({ n, passo, label }) {
  const done   = passo > n
  const active = passo === n
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6 }}>
      <div style={{
        width: 36, height: 36, borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center',
        fontWeight: 800, fontSize: '.9rem',
        background: done ? 'var(--cream)' : active ? 'var(--green)' : 'var(--gray-light)',
        color: done ? 'var(--green-dark)' : active ? '#fff' : 'var(--gray)',
        border: `2px solid ${done ? 'var(--cream-dark)' : active ? 'var(--green)' : 'var(--gray-light)'}`,
      }}>{done ? '✓' : n}</div>
      <span style={{ fontSize: '.72rem', fontWeight: 700, color: active ? 'var(--green)' : 'var(--gray)' }}>{label}</span>
    </div>
  )
}

function Card({ title, sub, children }) {
  return (
    <div className="form-card" style={{ background: '#fff', borderRadius: 18, boxShadow: 'var(--shadow)', padding: '32px 36px', marginBottom: 20 }}>
      {title && <div style={{ fontFamily: 'Playfair Display,serif', fontSize: '1.3rem', color: 'var(--green-dark)', marginBottom: 4 }}>{title}</div>}
      {sub   && <div style={{ fontSize: '.88rem', color: 'var(--gray)', marginBottom: 24 }}>{sub}</div>}
      {children}
    </div>
  )
}

function Campo({ label, erro, children }) {
  return (
    <div style={{ marginBottom: 20 }}>
      <label style={{ display: 'block', fontSize: '.85rem', fontWeight: 700, color: 'var(--dark)', marginBottom: 7 }}>{label}</label>
      {children}
      {erro && <div style={{ fontSize: '.78rem', color: 'var(--danger)', marginTop: 5 }}>{erro}</div>}
    </div>
  )
}

const inputSt = (erro) => ({
  width: '100%', padding: '12px 16px',
  border: `2px solid ${erro ? 'var(--danger)' : 'var(--gray-light)'}`,
  borderRadius: 12, fontFamily: 'Nunito,sans-serif', fontSize: '.95rem',
  color: 'var(--dark)', outline: 'none',
})

const btnPrimario = {
  width: '100%', background: 'var(--green)', color: '#fff', border: 'none',
  padding: '16px 32px', borderRadius: 50, fontFamily: 'Nunito,sans-serif',
  fontWeight: 900, fontSize: '1.05rem', cursor: 'pointer',
  transition: 'background .2s', marginTop: 8,
}
const btnSecundario = {
  width: '100%', background: 'transparent', color: 'var(--green)',
  border: '2px solid var(--green)', padding: '14px 32px', borderRadius: 50,
  fontFamily: 'Nunito,sans-serif', fontWeight: 800, fontSize: '.95rem',
  cursor: 'pointer', marginTop: 10,
}
