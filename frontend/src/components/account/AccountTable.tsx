import { useState } from "react";
import { Pagination, Table } from "react-bootstrap";
import { Pageable } from "../../models/Pageable";
import { Account } from "../../services/account/accountApi";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import { faTrash } from "@fortawesome/free-solid-svg-icons";

library.add(faTrash);

interface AccountTableRowProps {
  index: number; 
  data: Account;
  onDelete: () => Promise<void>;
}

function AccountTableRow({ index, data, onDelete }: AccountTableRowProps) {
  return (
    <tr key={index}>
      <td>{index}</td>
      <td>{data.number}</td>
      <td>{data.name}</td>
      <td>{data.balance}</td>
      <td onClick={onDelete}><FontAwesomeIcon icon="trash" /></td>
    </tr>
  );
}

interface AccountTableProps {
  pageable: Pageable<Account>;
  onDelete: (account: Account) => Promise<void>
}

export function AccountTable({ pageable, onDelete }: AccountTableProps) {
  const [currentPage, setCurrentPage] = useState(pageable.number);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  return (
    <div>
      <Table striped bordered hover>
      <thead>
        <tr>
          <th>#</th>
          <th>Account number</th>
          <th>Account name</th>
          <th>Account balance</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {pageable.content.map((data, index) => <AccountTableRow key={index} index={index} data={data} onDelete={() => onDelete(data)} />)}
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