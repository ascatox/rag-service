import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  baseUrl = 'http://localhost:8080';

  // Ask
  question = '';
  mode: 'concise' | 'detailed' | 'checklist' = 'concise';
  topK = 6;
  service = '';
  environment = '';
  tags = '';
  askAnswer = '';
  askCitations = '';
  askMeta = '';

  // Ingest
  path = 'docs/';
  ingestMode: 'full' | 'incremental' = 'full';
  include = '';
  exclude = '';
  ingestOut = '';

  async sendAsk(): Promise<void> {
    const payload = {
      question: this.question,
      mode: this.mode,
      topK: this.topK,
      filters: {
        service: this.service || undefined,
        environment: this.environment || undefined,
        tag: this.tags
          .split(',')
          .map(t => t.trim())
          .filter(Boolean)
      }
    };

    const res = await fetch(this.baseUrl.replace(/\/$/, '') + '/ask', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    const data = await res.json();
    this.askAnswer = data.answer || '';
    this.askCitations = (data.citations || []).map((c: any, i: number) =>
      `[${i + 1}] ${c.file} ${c.section || ''} ${c.lineStart}-${c.lineEnd}`
    ).join('\n');
    this.askMeta = `confidence=${data.confidence} latencyMs=${data.latencyMs}`;
  }

  async sendIngest(): Promise<void> {
    const toList = (value: string) => value
      .split(',')
      .map(s => s.trim())
      .filter(Boolean);

    const payload = {
      path: this.path,
      mode: this.ingestMode,
      include: toList(this.include),
      exclude: toList(this.exclude)
    };

    const res = await fetch(this.baseUrl.replace(/\/$/, '') + '/ingest', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    const data = await res.json();
    this.ingestOut = JSON.stringify(data, null, 2);
  }
}
