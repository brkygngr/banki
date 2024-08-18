import { Button, Col, Container, Form, InputGroup, Row } from 'react-bootstrap';
import {
  Account,
  CreateAccountRequest,
  DeleteAccountRequest,
  GetAccountsParams,
  UpdateAccountRequest,
} from '../../services/account/accountApi';
import { AccountTable } from './AccountTable';
import { Pageable } from '../../models/Pageable';
import { FormEvent, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { library } from '@fortawesome/fontawesome-svg-core';
import { faCirclePlus, faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { useSearchParams } from 'react-router-dom';

library.add(faCirclePlus, faMagnifyingGlass);

interface AccountsProps {
  accountPage: Pageable<Account>;
  onCreate: (request: CreateAccountRequest) => Promise<void>;
  onSearch: (params: GetAccountsParams) => Promise<void>;
  onEdit: (request: UpdateAccountRequest) => Promise<void>;
  onDelete: (request: DeleteAccountRequest) => Promise<void>;
}

export function Accounts({ accountPage, onCreate, onSearch, onEdit, onDelete }: AccountsProps) {
  const [searchParams, setSearchParams] = useSearchParams();

  const [newAccountName, setNewAccountName] = useState('');

  const handleSearchNumber = (number: string) => {
    setSearchParams((prev) => {
      prev.set('number', number);

      return prev;
    });
  };

  const handleSearchName = (name: string) => {
    setSearchParams((prev) => {
      prev.set('name', name);

      return prev;
    });
  };

  const handleSearchSubmit = async (e: FormEvent) => {
    e.preventDefault();

    const number = searchParams.get('number') ?? undefined;
    const name = searchParams.get('name') ?? undefined;

    await onSearch({ number, name });
  };

  const handleCreateSubmit = async (e: FormEvent) => {
    e.preventDefault();

    await onCreate({ name: newAccountName });
  };

  const handleEdit = async (account: Account, newName: string, newBalance: number) => {
    await onEdit({ id: account.id, name: newName, balance: newBalance });
  };

  const handleDelete = async (account: Account) => {
    await onDelete({ id: account.id });
  };

  return (
    <Container className="my-3">
      <Row className="justify-content-between">
        <Col xs={12} md={5}>
          <Form onSubmit={handleCreateSubmit}>
            <InputGroup className="mb-3">
              <InputGroup.Text>New account</InputGroup.Text>
              <Form.Control
                type="text"
                value={newAccountName}
                onChange={(e) => setNewAccountName(e.target.value)}
                placeholder="Name"
                maxLength={255}
                required
              />
              <Button variant="primary" type="submit">
                <FontAwesomeIcon className="me-1" icon="circle-plus" />
                Create
              </Button>
            </InputGroup>
          </Form>
        </Col>
        <Col xs={12} md={5}>
          <Form onSubmit={handleSearchSubmit}>
            <InputGroup className="mb-3">
              <InputGroup.Text>Account</InputGroup.Text>
              <Form.Control
                type="text"
                value={searchParams.get('number') ?? ''}
                onChange={(e) => handleSearchNumber(e.target.value)}
                placeholder="Number"
              />
              <Form.Control
                type="text"
                value={searchParams.get('name') ?? ''}
                onChange={(e) => handleSearchName(e.target.value)}
                placeholder="Name"
              />
              <Button variant="primary" type="submit">
                <FontAwesomeIcon className="me-1" icon="magnifying-glass" /> Search
              </Button>
            </InputGroup>
          </Form>
        </Col>
      </Row>
      <AccountTable pageable={accountPage} onEdit={handleEdit} onDelete={handleDelete} />
    </Container>
  );
}
