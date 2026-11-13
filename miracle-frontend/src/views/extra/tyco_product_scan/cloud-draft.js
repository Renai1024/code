// 新增：串行同步和固定operation支持“服务端已写入但响应丢失”的幂等重试。
export class CloudDraft {
  constructor(api, uuid, metadata = {}) {
    this.api = api; this.uuid = uuid; this.version = metadata.version || ''
    this.pending = metadata.pending || null
    this.queue = Promise.resolve()
  }
  metadata() { return { version: this.version, pending: this.pending } }
  // 新增：写入前通知调用者持久化pending，刷新后仍可用原操作标识重试，避免重复续期或错误冲突。
  save(data, before, after) {
    return this.enqueue(async () => {
      if (this.pending) {
        const previous = this.pending
        const result = await this.send(before, after)
        if (!previous.clear && JSON.stringify(previous.data) === JSON.stringify(data)) return result
      }
      // 新增：JSON快照兼容PDA旧版WebView，不依赖structuredClone。
      this.pending = { version: this.version, operation: this.uuid(), data: JSON.parse(JSON.stringify(data)) }
      return this.send(before, after)
    })
  }
  clear(before, after) {
    return this.enqueue(async () => {
      if (this.pending) await this.send(before, after)
      this.pending = { version: this.version, operation: this.uuid(), clear: true }
      return this.send(before, after)
    })
  }
  async send(before, after) {
    before?.()
    const pending = this.pending
    const result = await (pending.clear ? this.api.clear(pending) : this.api.save(pending))
    this.version = result.data.version; this.pending = null
    after?.(result.data)
    return result.data
  }
  enqueue(action) {
    const job = this.queue.catch(() => {}).then(action)
    this.queue = job
    return job
  }
}
