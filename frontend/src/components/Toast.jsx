import { useState, useCallback } from 'react'

// ===== HOOK useToast — use em qualquer página =====
export function useToast() {
  const [toast, setToast] = useState({ msg: '', erro: false, show: false })

  const mostrar = useCallback((msg, erro = false) => {
    setToast({ msg, erro, show: true })
    setTimeout(() => setToast(t => ({ ...t, show: false })), 3000)
  }, [])

  return { toast, mostrar }
}

// ===== COMPONENTE Toast =====
export default function Toast({ toast }) {
  return (
    <div style={{
      position: 'fixed', bottom: 28, right: 28,
      background: toast.erro ? 'var(--danger)' : 'var(--green-dark)',
      color: '#fff',
      padding: '13px 24px',
      borderRadius: 50,
      fontWeight: 700,
      fontSize: '.88rem',
      zIndex: 999,
      transform: toast.show ? 'translateY(0)' : 'translateY(80px)',
      opacity: toast.show ? 1 : 0,
      transition: 'all .35s cubic-bezier(.34,1.56,.64,1)',
      pointerEvents: 'none',
    }}>
      {toast.msg}
    </div>
  )
}
