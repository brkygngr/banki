import { useState } from "react";
import { Button, Form, InputGroup, Pagination, Table } from "react-bootstrap";
import { Pageable } from "../../models/Pageable";
import { Account } from "../../services/account/accountApi";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import { faCheck, faPen, faTrash, faX } from "@fortawesome/free-solid-svg-icons";

library.add(faTrash, faPen, faCheck, faX);

interface AccountTableNameEditProps {
  initialVal: string;
  onSubmit: (newName: string) => void;
  onCancel: () => void;
}

function AccountTableNameEdit({initialVal, onSubmit, onCancel}: AccountTableNameEditProps) {
  const [newAccountName, setNewAccountName] = useState(initialVal);

  return (
    <Form onSubmit={() => onSubmit(newAccountName)}>
      <InputGroup>
        <Form.Control
          type="text"
          value={newAccountName}
          onChange={(e) => setNewAccountName(e.target.value)}
          placeholder="Name"
          maxLength={255}
          required
        />
        <Button variant="primary" type="submit"><FontAwesomeIcon className="me-1" icon="check" />Finish</Button>
        <Button variant="secondary" type="button" onClick={() => onCancel()}><FontAwesomeIcon className="me-1" icon="x" />Cancel</Button>
      </InputGroup>
    </Form>
  );
}

interface AccountTableRowProps {
  index: number; 
  data: Account;
  onEdit: (newName: string) => Promise<void>;
  onDelete: () => Promise<void>;
}

function AccountTableRow({ index, data, onEdit, onDelete }: AccountTableRowProps) {
  const [editing, setEditing] = useState(false);

  const handleSubmit = (newName: string) => {
    setEditing(false);
    onEdit(newName);
  }

  const handleCancel = () => {
    console.log("handleCancel")
    setEditing(false);
  }

  return (
    <tr key={index}>
      <td colSpan={1}>{index}</td>
      <td colSpan={3}>{data.number}</td>
      <td colSpan={3}>{editing ? <AccountTableNameEdit initialVal={data.name} onSubmit={handleSubmit} onCancel={handleCancel} /> : data.name}</td>
      <td colSpan={3}>{data.balance}</td>
      <td colSpan={1}><div className="d-flex justify-content-around"><FontAwesomeIcon role="button" onClick={() => {
        if (editing) {
          handleCancel();
        } else {
          setEditing(true);
        }
      }} icon={editing ? "x" : "pen"} /><div className="vr"></div><FontAwesomeIcon role="button" onClick={onDelete} icon="trash" /></div></td>
    </tr> 
  );
}

interface AccountTableProps {
  pageable: Pageable<Account>;
  onEdit: (account: Account, newName: string) => Promise<void>;
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
        {pageable.content.map((data, index) => <AccountTableRow key={index} index={index} data={data} onEdit={(newName) => onEdit(data, newName)} onDelete={() => onDelete(data)} />)}
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
  )
}