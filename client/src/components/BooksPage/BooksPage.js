import React, { Component } from 'react'
import BookTable from '../BookTable/BookTable'
import './BooksPage.css'

export default class HomePage extends Component {

    constructor(props) {
        super(props)

        this.state = {
            authorFilter: "",
            searchField: ""
        }

        this.setSearchField = e => {
            this.setState({
                searchField: e.target.value
            })
        }

        this.setAuthorFilter = () => {
            this.setState({
                authorFilter: this.state.searchField
            })
        }

        this.clearAuthorFilter = () => {
            this.setState({
                authorFilter: "",
                searchField: ""
            })
        }
    }
    render() {
        return (
            <div className='BooksPage flexColumn alignCenter'>
                <div id='searchDiv'>
                    <input type='text'
                        id='searchInput'
                        placeholder='search author ...'
                        onChange={this.setSearchField}
                        value={this.state.searchField} />
                    <button class='brightButton' id='searchButton' onClick={this.setAuthorFilter}>Search</button>
                    <button class='brightButton' id='searchButton' onClick={this.clearAuthorFilter}>Clear</button>
                </div>
                <BookTable
                    user={this.props.user}
                    orderItem={this.props.orderItem}
                    authorFilter={this.state.authorFilter}
                />
            </div>
        )
    }
}
