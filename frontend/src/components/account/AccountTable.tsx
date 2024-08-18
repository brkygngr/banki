import { library } from '@fortawesome/fontawesome-svg-core';
import {
  faArrowUpRightFromSquare,
  faCheck,
  faPen,
  faTrash,
  faX,
} from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useState } from 'react';
import { Button, Form, InputGroup, Pagination, Table } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { Pageable } from '../../models/Pageable';
import { Account } from '../../services/account/accountApi';

library.add(faTrash, faPen, faCheck, faX, faArrowUpRightFromSquare);

interface AccountTableNameEditProps {
  index: number;
  data: Account;
  onSubmit: (newName: string, newBalance: number) => void;
  onCancel: () => void;
}

function AccountTableRowEdit({ index, data, onSubmit, onCancel }: AccountTableNameEditProps) {
  const [newName, setNewName] = useState(data.name);
  const [newBalance, setNewBalance] = useState(parseInt(data.balance, 10));

  const handleSubmit = () => {
    if (!newName || !newBalance) {
      alert('Name and balance is required!');
      return;
    }

    onSubmit(newName, newBalance);
  };

  return (
    <tr key={index}>
      <td colSpan={1}>{index}</td>
      <td colSpan={3}>{data.number}</td>
      <td colSpan={3}>
        <InputGroup>
          <Form.Control
            type="text"
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
            placeholder="Name"
            maxLength={255}
            required
          />
        </InputGroup>
      </td>
      <td colSpan={3}>
        <InputGroup>
          <Form.Control
            type="number"
            value={newBalance}
            onChange={(e) => setNewBalance(parseInt(e.target.value, 10))}
            placeholder="Balance"
            min={0}
            required
          />
        </InputGroup>
      </td>
      <td colSpan={1}>
        <div className="d-flex justify-content-around">
          <Button variant="primary" type="button" onClick={handleSubmit} size="sm">
            <FontAwesomeIcon className="me-1" icon="check" />
            Finish
          </Button>
          <div className="vr"></div>
          <Button variant="secondary" type="button" onClick={onCancel} size="sm">
            <FontAwesomeIcon role="button" icon="x" />
            Cancel
          </Button>
        </div>
      </td>
    </tr>
  );
}

interface AccountTableNameProps {
  id: string;
  name: string;
}

function AccountTableName({ id, name }: AccountTableNameProps) {
  return (
    <div>
      <Link to={`/accounts/${id}`}>
        <strong>{name}</strong>
        <FontAwesomeIcon className="ms-1" icon="arrow-up-right-from-square" />
      </Link>
    </div>
  );
}

interface AccountTableRowProps {
  index: number;
  data: Account;
  onEdit: (newName: string, newBalance: number) => Promise<void>;
  onDelete: () => Promise<void>;
}

function AccountTableRow({ index, data, onEdit, onDelete }: AccountTableRowProps) {
  const [editing, setEditing] = useState(false);

  const handleSubmit = (newName: string, newBalance: number) => {
    setEditing(false);
    onEdit(newName, newBalance);
  };

  const handleCancel = () => {
    setEditing(false);
  };

  if (editing) {
    return (
      <AccountTableRowEdit
        index={index}
        data={data}
        onSubmit={handleSubmit}
        onCancel={handleCancel}
      />
    );
  }

  return (
    <tr key={index}>
      <td colSpan={1}>{index}</td>
      <td colSpan={3}>{data.number}</td>
      <td colSpan={3}>
        <AccountTableName id={data.id} name={data.name} />
      </td>
      <td colSpan={3}>{data.balance}</td>
      <td colSpan={1}>
        <div className="d-flex justify-content-around">
          <FontAwesomeIcon role="button" onClick={() => setEditing(true)} icon="pen" />
          <div className="vr"></div>
          <FontAwesomeIcon role="button" onClick={onDelete} icon="trash" />
        </div>
      </td>
    </tr>
  );
}

interface AccountTableProps {
  pageable: Pageable<Account>;
  onEdit: (account: Account, newName: string, newBalance: number) => Promise<void>;
  onDelete: (account: Account) => Promise<void>;
}

export function AccountTable({ pageable, onEdit, onDelete }: AccountTableProps) {
  const [currentPage, setCurrentPage] = useState(pageable.number);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  return (
    <div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th colSpan={1}>#</th>
            <th colSpan={3}>Account number</th>
            <th colSpan={3}>Account name</th>
            <th colSpan={3}>Account balance</th>
            <th colSpan={1}></th>
          </tr>
        </thead>
        <tbody>
          {pageable.content.map((data, index) => (
            <AccountTableRow
              key={index}
              index={index}
              data={data}
              onEdit={(newName, newBalance) => onEdit(data, newName, newBalance)}
              onDelete={() => onDelete(data)}
            />
          ))}
        </tbody>
      </Table>
      <Pagination>
        {[...Array(pageable.totalPages)].map((_, index) => (
          <Pagination.Item
            key={index}
            active={index === currentPage}
            onClick={() => handlePageChange(index)}
          >
            {index + 1}
          </Pagination.Item>
        ))}
      </Pagination>
    </div>
  );
}
