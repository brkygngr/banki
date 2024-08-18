import { IconProp, library } from '@fortawesome/fontawesome-svg-core';
import { faCircleXmark, faThumbsUp, faWarning } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Toast } from 'react-bootstrap';

library.add(faThumbsUp, faWarning, faCircleXmark);

const iconMap: Record<AppToastType, IconProp> = {
  success: 'thumbs-up',
  warning: 'warning',
  failure: 'circle-xmark',
};

const timeMap: Record<number, string> = {
  60: 'seconds',
  3600: 'minutes',
  86400: 'hours',
};

export const enum AppToastType {
  SUCCESS = 'success',
  WARNING = 'warning',
  FAILURE = 'failure',
}

interface AppToastProps {
  header: string;
  message: string;
  timestamp: Date;
  type: AppToastType;
  durationMS: number;
  onClose: () => void;
}

function pastTimeType(timestamp: Date): string {
  const secondsPast = Math.floor((Date.now() - timestamp.getTime()) / 1000);

  for (const [timeBreakpoint, pastTimeType] of Object.entries(timeMap)) {
    const breakpoint = parseInt(timeBreakpoint, 10);

    if (secondsPast < breakpoint) {
      return pastTimeType;
    }
  }

  return 'days';
}

export function AppToast({ header, message, timestamp, type, durationMS, onClose }: AppToastProps) {
  return (
    <Toast onClose={onClose} delay={durationMS} autohide>
      <Toast.Header>
        <FontAwesomeIcon icon={iconMap[type]} />
        <strong className="me-auto">{header}</strong>
        <small>{pastTimeType(timestamp)} ago</small>
      </Toast.Header>
      <Toast.Body>{message}</Toast.Body>
    </Toast>
  );
}
