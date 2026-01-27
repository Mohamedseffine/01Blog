import { Directive, EventEmitter, HostListener, Input, Output } from '@angular/core';

@Directive({
  selector: '[appDebounceClick]',
  standalone: true
})
export class DebounceClickDirective {
  @Input() appDebounceTime = 650;
  @Output('appDebounceClick') debouncedClick = new EventEmitter<Event>();

  private isCooling = false;

  @HostListener('click', ['$event'])
  handleClick(event: Event) {
    if (this.isCooling) {
      event.preventDefault();
      event.stopImmediatePropagation();
      return;
    }

    this.isCooling = true;
    this.debouncedClick.emit(event);
    setTimeout(() => {
      this.isCooling = false;
    }, this.appDebounceTime);
  }
}
