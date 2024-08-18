import { useState } from "react";
import { Pagination, Table } from "react-bootstrap";
import { Pageable } from "../../models/Pageable";
import { Account } from "../../services/account/accountApi";

interface AccountTableRowProps {
  index: number; 
  data: Account;
}

function AccountTableRow({ index, data }: AccountTableRowProps) {
  return (
    <tr key={index}>
      <td>{index}</td>
      <td>{data.number}</td>
      <td>{data.name}</td>
      <td>{data.balance}</td>
    </tr>
  );
}

interface AccountTableProps {
  pageable: Pageable<Account>;
}

export function AccountTable({ pageable }: AccountTableProps) {
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
        </tr>
      </thead>
      <tbody>
        {pageable.content.map((data, index) => <AccountTableRow key={index} index={index} data={data} />)}
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