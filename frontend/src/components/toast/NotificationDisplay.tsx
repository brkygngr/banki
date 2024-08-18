import { useDispatch, useSelector } from "react-redux";
import { deleteMessage, ErrorMessage } from "../../services/error/errorSlice";
import { RootState } from "../../services/store";
import { AppToast } from "./AppToast";
import './NotificationDisplay.css';

export function NotificationDisplay() {
  const messages = useSelector((state: RootState) => state.errorSlice.messages);
  const dispatch = useDispatch();

  const handleClose = (message: ErrorMessage) => {
    dispatch(deleteMessage({ message }));
  }

  return (
    <div className="notification-display">
     {messages.map((message, index) => {
      return <AppToast key={index + "_" + message.timestamp} header={message.header} message={message.message} timestamp={new Date(message.timestamp)} type={message.type} durationMS={message.durationMS} onClose={() => handleClose(message)} />
    })}
    </div>
  );
}