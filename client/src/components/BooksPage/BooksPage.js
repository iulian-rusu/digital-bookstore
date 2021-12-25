import React, { Component } from 'react'
import BookTable from '../BookTable/BookTable'
import './BooksPage.css'

export default class HomePage extends Component {

    constructor(props) {
        super(props)

        this.state = {
            searchField: "",
            filter: {
                field: "authors",
                value: ""
            }
        }

        this.setSearchField = e => {
            this.setState({
                searchField: e.target.value
            })
        }

        this.setFilter = () => {
            this.setState({
                filter: {
                    field: this.state.filter.field,
                    value: this.state.searchField
                },
            })
        }

        this.clearFilter = () => {
            this.setState({
                filter: {
                    field: this.state.filter.field,
                    value: ""
                },
                searchField: ""
            })
        }

        this.setField = event => {
            this.setState({
                filter: {
                    field: event.target.value,
                    value: this.state.filter.value
                }
            })
        }
    }
    render() {
        return (
            <div className='BooksPage flexColumn alignCenter'>
                <div id='searchDiv'>
                    <select id="filterType" onChange={this.setField}>
                        <option>authors</option>
                        <option>title</option>
                    </select>
                    <input type='text'
                        id='searchInput'
                        placeholder={'search ' + this.state.filter.field}
                        onChange={this.setSearchField}
                        value={this.state.searchField} />
                    <button className='brightButton' id='searchButton' onClick={this.setFilter}>Search</button>
                    <button className='brightButton' id='searchButton' onClick={this.clearFilter}>Clear</button>
                </div>
                <BookTable
                    user={this.props.user}
                    orderItem={this.props.orderItem}
                    filter={this.state.filter}
                />
            </div>
        )
    }
}
