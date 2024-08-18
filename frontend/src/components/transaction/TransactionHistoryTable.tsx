import { Table } from 'react-bootstrap';
import { TransactionHistoryResponse } from '../../services/transaction/transactionApi';

export interface TransactionHistoryRowProps {
  index: number;
  history: TransactionHistoryResponse;
}

export function TransactionHistoryRow({ index, history }: TransactionHistoryRowProps) {
  return (
    <tr>
      <td>{index}</td>
      <td>{history.from.name}</td>
      <td>{history.to.name}</td>
      <td>{history.amount}</td>
      <td>{history.transactionDate}</td>
      <td>{history.status}</td>
    </tr>
  );
}

export interface TransactionHistoryTableProps {
  histories: TransactionHistoryResponse[];
}

export function TransactionHistoryTable({ histories }: TransactionHistoryTableProps) {
  return (
    <Table striped bordered hover>
      <thead>
        <tr>
          <th>#</th>
          <th>From</th>
          <th>To</th>
          <th>Amount</th>
          <th>Date</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        {histories.map((history, index) => (
          <TransactionHistoryRow key={history.id} index={index} history={history} />
        ))}
      </tbody>
    </Table>
  );
}
