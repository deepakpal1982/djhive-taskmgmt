import { createApi } from '@reduxjs/toolkit/query/react'
import { authBaseQuery } from '../auth'

export const api = createApi({
  reducerPath: 'projects',
  baseQuery: authBaseQuery({ path: 'api/v1/projects' }),
  tagTypes: ['Project'],
  endpoints: (builder) => ({
    getProjects: builder.query({
      query: () => '/',
      providesTags: ['Project'],
    }),
    addProject: builder.mutation({
      query: (project) => ({
        url: '/',
        method: 'POST',
        body: project,
      }),
      invalidatesTags: ['Project'],
    }),
    updateProject: builder.mutation({
      query: (project) => ({
        url: `/${project.id}`,
        method: 'PUT',
        body: project,
      }),
      invalidatesTags: ['Project'],
    }),
  }),
})
